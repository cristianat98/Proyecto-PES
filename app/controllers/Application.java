
package controllers;

import jdk.nashorn.internal.runtime.regexp.joni.ast.CClassNode;
import play.db.jpa.JPABase;
import play.mvc.*;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import models.*;

import javax.validation.Valid;


@With(Secure.class)
public class Application extends Controller {

	static int visionadmin = 0;

	@Before
	static void addUser() {
		Cliente user = connected();

		if(user != null) {
			renderArgs.put("client", user);
		}
	}

	static Cliente connected() {
		if(renderArgs.get("client") != null) {
			return renderArgs.get("client", Cliente.class);
		}

		String username = session.get("user");
		if(username != null) {
			return Cliente.find("byUsuario", username).first();
		}

		return null;
	}

	public static void loginTemplate(){
			render();
    }
	
	public static void MostrarPrenda(Long id){

		Prenda prenda = Prenda.findById(id);
		renderBinary(prenda.imagen.get());
	}

	public static void MostrarPerfil(){

		Cliente c = Cliente.find("byUsuario", session.get("user")).first();
		c.perfil.toString();
		if (c.perfil.get() == null)
			renderBinary(new File("C:/Users/cristian/Desktop/play-1.5.3/Proyecto-PES/public/images/avatar.jpg"));

		else
			renderBinary(c.perfil.get());
	}

    public static void register() {
	        render();
	}

	public static void recuperacionContra() {
			render();
	}
	
	public static void ModificarUsuario() {
		renderArgs.put("modificar", 0);
		   render();
	}
	  
	public static void EliminarUsuario () {
		   render();
	}

	public static void CambiarVistaNormal(){
		index();
	}

	public static void CambiarVistaAdmin(){
		  visionadmin = 0;
		  renderArgs.put("visionadmin", visionadmin);
		  renderTemplate("Application/principalAdmin.html");
	}

	public static void Logout (){
		   session.clear();
	       renderArgs.put("client",null);
	       renderTemplate("Application/loginTemplate.html");
	}


	//ANDROID
	public static void registrarAndroid(String user, String password) {
		 Cliente c = Cliente.find("byUsuario",user).first();
		 if(c==null) {
			 c= new Cliente(user,password);
			 c._save();	   
			 renderText("OK, Cliente se puede registrar, añadido a la BD");
		 }
		 else
			   renderText("FAIL, este nombre de usuario ya existe");
	}
	   
	public static void loginAndroid(String user, String password){
		Cliente c = Cliente.find("byUsuarioAndContraseña", user, password).first();
		if(c!=null) {
			renderText("OK ,este cliente esta en la BD");
		}
		else 
				renderText("FAIL este cliente no esta en la BD ");	
		}

	public static void index() {

		if(connected() != null) {
			List<Prenda> camisetas =Prenda.find("byTipo", "CAMISETA").fetch();
			List<Prenda> pantalones =Prenda.find("byTipo", "PANTALON").fetch();
			renderArgs.put("camisetas",camisetas);
			renderArgs.put("pantalones",pantalones);
			render("Application/principal.html");
		}
		else {
			renderTemplate("Application/loginTemplate.html");
		}
    }
	
	public static void Login(@Valid Cliente cliente, String adm) {

		Cliente c = Cliente.find("byUsuarioAndContraseña", cliente.usuario, cliente.contraseña).first();
		if (c != null) {
			if (adm.equals("Entrar Como Administrador")) {
				if (c.admin == 0) {
					String erroradmin = "erroradmin";
					renderArgs.put("erroradmin", erroradmin);
					renderTemplate("Application/loginTemplate.html");
				}

				else{
					session.put("user",cliente.usuario);
					renderArgs.put("client", c);
					renderArgs.put("visionadmin", visionadmin);
					renderArgs.put("client", c);
					renderTemplate("Application/principalAdmin.html");
				}
			}

			else{
				session.put("user",cliente.usuario);
				renderArgs.put("client", c);
				index();
			}

		}
		else{
			String error = "error";
			renderArgs.put("error", error);
			renderTemplate("Application/loginTemplate.html");
		}
	}

	public static void registrarCliente(@Valid Cliente nuevocliente, String usuario, String contraseña, String mail, String nombre, String apellido1) {

		   validation.required(usuario);
		   validation.required(contraseña);
		   validation.required(mail);
		   validation.required(nombre);
		   validation.required(apellido1);
		   validation.equals(contraseña, nuevocliente.contraseña).message("Las contraseñas no coinciden");
		   if(validation.hasErrors())
			   render("@register");


		   Cliente c = Cliente.find("byUsuario", usuario).first();
		   if (c == null) {
		   	   nuevocliente.usuario = usuario;
		   	   nuevocliente.mail = mail;
		   	   nuevocliente.nombre = nombre;
		   	   nuevocliente.apellido1 = apellido1;
			   nuevocliente.create();
			   session.put("user", nuevocliente.usuario);
			   index();
		   }

		   else{
			   validation.equals(usuario, "").message("El usuario ya está en uso");
			   render("@register");
		   }
	}

	public static void recuperarContra(@Valid Cliente cliente, String mail) {
		   validation.required(mail);
		   validation.equals(mail, cliente.mail).message("Los emails no coinciden");
		   if(validation.hasErrors()) {
			   render("@recuperacionContra",cliente, mail);
		   }
		   else {
			   Cliente c= Cliente.find("byMail", cliente.mail).first();
			   if(c!=null) {
				   renderText("La contraeña es:" +c.contraseña);
			   }
		   }
	 
	}

   public static void eliminarusuariocontraseña (String contraseña){
	   validation.required(contraseña);
	   String username = session.get("user");
	   Cliente c=Cliente.find("byUsuario", username).first();
	   validation.equals(contraseña, c.contraseña).message("La contraseña es incorrecta, no puede eliminar su cuenta");
	   if(validation.hasErrors()) {
		   render("@EliminarUsuario");
	   }
	   else {
		   c.delete();
		   session.clear();
		   renderTemplate("Application/loginTemplate.html");
	   }
   }

   public static void cargardatosusuario(String contraseña){
	   int i = 0;
	   validation.required(contraseña);
	   String username = session.get("user");
	   Cliente usuario=Cliente.find("byUsuario", username).first();
	   validation.equals(contraseña, usuario.contraseña).message("La contraseña es incorrecta, no puede modificar datos");
	   renderArgs.put("modificar", i);
	   if(validation.hasErrors()) {
		   render("@ModificarUsuario");
	   }
	   else{
	   	renderArgs.put("modificar", 1);
	   	renderArgs.put("clienteM", usuario);   //Variable en HTML , Variable en java//
		renderTemplate("Application/ModificarUsuario.html");
	   }
   }

   public static void ModificarDatos2(Cliente clienteM, String contraseña) {

	   String username = session.get("user");
	   Cliente c=Cliente.find("byUsuario", username).first();
	   validation.equals(contraseña, clienteM.contraseña).message("Las contraseñas no coinciden");
	   if(validation.hasErrors()) {
	   	int i = 1;
	   	renderArgs.put("clienteM", c);
	   	renderArgs.put("modificar", 1);
		   render("@ModificarUsuario");
	   }

	   if(c!=null) {
	   	if (!clienteM.usuario.equals("")){
			c.usuario=clienteM.usuario;
			session.put("user", c.usuario);
		}

	   	if (!clienteM.contraseña.equals(""))
	   		c.contraseña=clienteM.contraseña;

	   	if (!clienteM.mail.equals(""))
	   		c.mail=clienteM.mail;

	   	if (!clienteM.nombre.equals(""))
	   		c.nombre=clienteM.nombre;

	   	if (!clienteM.apellido1.equals(""))
	   		c.apellido1=clienteM.apellido1;

	   	if (!clienteM.apellido2.equals(""))
	   		c.apellido2=clienteM.apellido2;

	   	if (clienteM.perfil != null)
	   		c.perfil = clienteM.perfil;

		   c.save();
		   index();
	   }
   }


   //FUNCIONES PAG.ADMIN BARRA

   //AÑADIR USUARIO
	public static void añadirusuario() {

		if (visionadmin != 1)
		visionadmin=1;

		else
			visionadmin=0;

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

   public static void registrarclienteadmin(@Valid Cliente nuevocliente, String usuario, String nombre, String apellido1, String contraseña, String mail){

	   validation.required(usuario);
	   validation.required(nombre);
	   validation.required(apellido1);
	   validation.required(contraseña);
	   validation.required(mail);
	   validation.equals(contraseña, nuevocliente.contraseña).message("Las contraseñas no coinciden");
	   if(validation.hasErrors()) {
		   renderArgs.put("visionadmin", visionadmin);
		   render("Application/principalAdmin.html");
	   }
	   Cliente c = Cliente.find("byUsuario", usuario).first();
	   if (c == null) {
	   		nuevocliente.nombre = nombre;
	   		nuevocliente.apellido1 = apellido1;
	   		nuevocliente.usuario = usuario;
	   		nuevocliente.mail = mail;
	   		nuevocliente.create();
	   		renderArgs.put("visionadmin", visionadmin);
	   		validation.equals(usuario, "").message("Usuario registrado correctamente");
	   		render("Application/principalAdmin.html");
	   }

	   else{
		   renderArgs.put("visionadmin", visionadmin);
		   validation.equals(usuario, "").message("El usuario ya está en uso");
		   renderTemplate("Application/principalAdmin.html");
	   }
   }


   //MODIFICAR USUARIO
	public static void modificarusuarioadmin(){

		if (visionadmin != 2){
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			visionadmin = 2;
		}

		else{
			visionadmin = 0;
		}

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");

	}

	public static void cargardatos(Long idusuario){
		visionadmin = 3;
		List<Cliente> lclientes = Cliente.findAll();
		renderArgs.put("listaclientes", lclientes);
		Cliente modificar = Cliente.findById(idusuario);
		renderArgs.put("usuarioM",modificar);
		renderArgs.put("visionadmin", visionadmin);
		renderTemplate ("Application/principalAdmin.html");
	}

	public static void ModificarU(Cliente usuarioM, String usuarioinicial, String usuariofinal){

		Cliente c = Cliente.find("byUsuario", usuariofinal).first();

		if (c != null){
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			renderArgs.put("visionadmin", visionadmin);
			c = Cliente.find("byUsuario", usuarioinicial).first();
			renderArgs.put("usuarioM", c);
			validation.equals(usuariofinal, "").message("El usuario ya está en uso");
			renderTemplate("Application/principalAdmin.html");
		}

		c = Cliente.find("byUsuario", usuarioinicial).first();
		if(c!=null) {
			if (!usuariofinal.equals("")){
				if (usuarioinicial.equals(session.get("user")))
					session.put("user", usuariofinal);

				c.usuario = usuariofinal;
			}

			if (!usuarioM.contraseña.equals(""))
				c.contraseña = usuarioM.contraseña;

			if (!usuarioM.mail.equals(""))
				c.mail = usuarioM.mail;

			if (!usuarioM.nombre.equals(""))
				c.nombre = usuarioM.nombre;

			if (!usuarioM.apellido1.equals(""))
				c.apellido1 = usuarioM.apellido1;

			if (!usuarioM.apellido2.equals(""))
				c.apellido2 = usuarioM.apellido2;

			if (usuarioM.perfil != null)
				c.perfil = usuarioM.perfil;

			c.save();
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			visionadmin = 2;
			renderArgs.put("client", c);
			renderArgs.put("visionadmin", visionadmin);
			renderTemplate("Application/principalAdmin.html");
		}
	}

	
	//ELIMINAR USUARIO
	public static void cargareliminar(){

		if (visionadmin != 4){
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			visionadmin = 4;
		}

		else{
			visionadmin = 0;
		}

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

	public static void eliminarusuarioadmin (Long idusuario){

		visionadmin = 4;
		Cliente eliminar = Cliente.findById(idusuario);
		eliminar.delete();
		List<Cliente> lclientes = Cliente.findAll();
		renderArgs.put("listaclientes", lclientes);
		renderArgs.put("visionadmin", visionadmin);
		renderTemplate ("Application/principalAdmin.html");
	}


	//AÑADIR STOCK
   public static void goAddStock(){
		if (visionadmin != 5)
			visionadmin = 5;

		else
			visionadmin=0;

		renderArgs.put("visionadmin",visionadmin);
		renderTemplate("Application/principalAdmin.html");
	}

   public static void AddStock(Prenda prendaM){

	   prendaM.equipo=prendaM.equipo.toUpperCase();
	   prendaM.tipo=prendaM.tipo.toUpperCase();
	   prendaM.talla = prendaM.talla.toUpperCase();
	   Prenda p = Prenda.find("byTipoAndEquipoAndTallaAndPrecioAndAño",prendaM.tipo,prendaM.equipo,prendaM.talla,prendaM.precio, prendaM.año).first();


	   if(p==null){
		   prendaM.create();
		   renderArgs.put("visionadmin", visionadmin);
		   renderTemplate("Application/principalAdmin.html");
	   }

	   else {
		   p.cantidadStock = p.cantidadStock + prendaM.cantidadStock;
		   p.imagen = prendaM.imagen;
		   p.save();
		   renderArgs.put("visionadmin", visionadmin);
		   renderTemplate("Application/principalAdmin.html");
	   }

   }


   //QUITAR STOCK
   public static void quitarstock(){

		if (visionadmin != 6)
			visionadmin = 6;

		else
			visionadmin = 0;

		List<Prenda> lprendas = Prenda.findAll();
		List<String> lequipos = new ArrayList<String>();
	    lequipos.add(lprendas.get(0).equipo);
		boolean encontrado = false;
		int j;
		for(int i = 0; i<lprendas.size();i++){

			j = 0;

			while (j<lequipos.size() && !encontrado){
				if (lprendas.get(i).equipo == lequipos.get(j))
					encontrado = true;
				j++;
			}

			if (!encontrado)
				lequipos.add(lprendas.get(i).equipo);

			else
				encontrado = false;

		}

		renderArgs.put("listaequipos", lequipos);
		renderArgs.put("visionadmin", visionadmin);
	    renderTemplate("Application/principalAdmin.html");
  }
   
   public static void cargarequipo(Long idequipo){
		renderArgs.put("visionadmin", visionadmin);
	   renderTemplate("Application/principalAdmin.html");
   }

   public static void ModificarDatosAdmin(Cliente user, String username){

	   Cliente client = Cliente.find("byUsuario", username).first();
	   if (client != null) {
		   client.usuario = user.usuario;
		   client.contraseña = user.contraseña;
		   client.mail = user.mail;
		   client.save();
		   List<Cliente> lclientes = Cliente.findAll();
		   renderArgs.put("listaclientes", lclientes);
		   renderTemplate ("Application/principalAdmin.html");
	   }
   }

   public static void comprar (String tipo, String equipo, String talla, int cantidad, String usuario, String contraseña){

		if (tipo == null || equipo == null || talla == null || cantidad < 1 || usuario == null || contraseña == null)
				renderText("No has introducido todos los datos.");

			else {
			Cliente c = Cliente.find("byUsuarioAndContraseña", usuario, contraseña).first();

			if (c == null)
				renderText("Ese usuario no existe");
			else
				{
				Prenda p = Prenda.find("byTipoAndEquipoAndTalla", tipo, equipo, talla).first();
				if (p != null) {
					if (p.getCantidadStock() >= cantidad) {
						Date fecha = new Date();
						p.setCantidadStock(p.getCantidadStock() - cantidad);
						Compra Compra = new Compra(c, p, fecha);
						Compra.save();
						renderText("Has comprado " + cantidad + " " + tipo + " del " + equipo);
					} else
						renderText("No tenemos tanto stock en la tienda. Vuelve a realizar el pedido con el número disponible.");
				} else
					renderText("No tenemos esa vestimenta disponible actualmente.");
			}
		}
	}
}