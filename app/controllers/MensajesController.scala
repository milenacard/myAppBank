package controllers

import javax.inject._

import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo._
import reactivemongo.api.ReadPreference
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import utils.Autenticador

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MensajesController @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit exec: ExecutionContext) extends Controller with MongoController with ReactiveMongoComponents {

  def mensajesFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection]("MensajesGo"))
  def ChatStatusFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection]("ChatStatus"))  

    def find = Action.async {
      val cursor: Future[List[JsObject]] = mensajesFuture.flatMap{ mensajes => 
        mensajes.find(Json.obj())
          //.sort(Json.obj("documento" -> -1))
          .cursor[JsObject](ReadPreference.primary).collect[List]()
      }
      cursor.map { mensajes =>
        Ok(Json.toJson(mensajes)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
      }
    }
    def findChats = Action.async {
      val cursor: Future[List[JsObject]] = ChatStatusFuture.flatMap{ ChatStatus => 
        ChatStatus.find(Json.obj())
          //.sort(Json.obj("documento" -> -1))
          .cursor[JsObject](ReadPreference.primary).collect[List]()
      }
      cursor.map { ChatStatus =>
        Ok(Json.toJson(ChatStatus)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
      }
    }
    
    def findMessagesByMail(correo:String) = Action.async {
    val auth = new Autenticador
    //Logger.info(documentType)
      val cursor: Future[List[JsObject]] = mensajesFuture.flatMap{ mensajes =>
      mensajes.find(Json.obj("para" -> correo))
      // sort them by creation date
      //.sort(Json.obj("documento" -> -1)).
      // perform the query and get a cursor of JsObject
     .cursor[JsObject](ReadPreference.primary).collect[List]()
      //SI el token no es valido retorna un JsonObject Vacio
    }
    //Si es valido retorna el query
    if(auth.validarToken(token)){Logger.info("Valid Token")
     cursor.map { mensajes =>
      Ok(Json.toJson(mensajes)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")}
    //Si es invalido retorna un error. Y si no encuentra nada en la query es porque no existe.,
    }else{
        Logger.info("Invalid token")
        cursor.map { mensajes =>
      Ok(Json.obj("err"->"Invalid Token")).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")}
    
    }
  }
  
    
    def findNewMessagesByMail(correo:String) = Action.async {
    val auth = new Autenticador
    //Logger.info(documentType)
      val cursor: Future[List[JsObject]] = mensajesFuture.flatMap{ mensajes =>
      mensajes.find(Json.obj("para" -> correo,"estado"->"nuevo"))
      // sort them by creation date
      //.sort(Json.obj("documento" -> -1)).
      // perform the query and get a cursor of JsObject
     . cursor[JsObject](ReadPreference.primary).collect[List]()
      //SI el token no es valido retorna un JsonObject Vacio
    }
    //Si es valido retorna el query
    if(auth.validarToken(token)){Logger.info("Valid Token")
     cursor.map { mensajes =>
      Ok(Json.toJson(mensajes)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")}
    //Si es invalido retorna un error. Y si no encuentra nada en la query es porque no existe.,
    }else{
        Logger.info("Invalid token")
        cursor.map { mensajes =>
      Ok(Json.obj("err"->"Invalid Token")).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")}
    
    }
  }
   
 def updateReadMessage = Action.async {request =>
    val json = Json.obj(
      "$set" -> Json.obj(
        "estado"->"leido"
        )
    )

    val selector = Json.obj(
        "de" -> request.body.asFormUrlEncoded.get("de")(0),
        "para" -> request.body.asFormUrlEncoded.get("para")(0),
        "fecha" -> request.body.asFormUrlEncoded.get("fecha")(0)
    )

    for {
      mensajes <- mensajesFuture
      lastError <- mensajes.update(selector,json)
    } yield Ok("Mensaje Leído")
  }  
   
    
    def createNewMessage = Action.async { request =>

      val json = Json.obj(
        "de" -> request.body.asFormUrlEncoded.get("de")(0),
        "para" -> request.body.asFormUrlEncoded.get("para")(0),
        "fecha" -> request.body.asFormUrlEncoded.get("fecha")(0),
        "estado" -> request.body.asFormUrlEncoded.get("estado")(0),
        "mensaje" -> request.body.asFormUrlEncoded.get("mensaje")(0)
      )

      for {
        mensajes <- mensajesFuture
        lastError <- mensajes.insert(json)
      } yield Ok("Mongo LastError: %s" . format(lastError))

    }
 
  def connect = Action.async {request =>
    val json = Json.obj(
      "$set" -> Json.obj(
        "estado"->"conectado"
        )
    )

    val selector = Json.obj(
      "correo" -> request.body.asFormUrlEncoded.get("correo")(0)
    )

    for {
      ChatStatus <- ChatStatusFuture
      lastError <- ChatStatus.update(selector,json)
    } yield Ok("Mongo LastError: %s" . format(lastError))
  }
 
  def disconnect = Action.async {request =>
    val json = Json.obj(
      "$set" -> Json.obj(
        "estado"->"desconectado"
    )
    )
    val selector = Json.obj(
      "correo" -> request.body.asFormUrlEncoded.get("correo")(0)
    )

    for {
      ChatStatus <- ChatStatusFuture
      lastError <- ChatStatus.update(selector,json)
    } yield Ok("Mongo LastError: %s" . format(lastError))
    }
  
  
    def getStatusByMail(correo:String) = Action.async {
    val auth = new Autenticador
    //Logger.info(documentType)
      val cursor: Future[List[JsObject]] = ChatStatusFuture.flatMap{ ChatStatus =>
      ChatStatus.find(Json.obj("correo" -> correo))
      // sort them by creation date
      //.sort(Json.obj("documento" -> -1)).
      // perform the query and get a cursor of JsObject
     . cursor[JsObject](ReadPreference.primary).collect[List]()
      //SI el token no es valido retorna un JsonObject Vacio
    }
    //Si es valido retorna el query
    if(auth.validarToken(token)){Logger.info("Valid Token")
     cursor.map { ChatStatus =>
      Ok(Json.toJson(ChatStatus)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")}
    //Si es invalido retorna un error. Y si no encuentra nada en la query es porque no existe.,
    }else{
        Logger.info("Invalid token")
        cursor.map { ChatStatus =>
      Ok(Json.obj("err"->"Invalid Token")).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")}
    
    }
    }
  
 

/**
  

  def update = Action.async { request =>

    val json = Json.obj(
      "$set" -> Json.obj(
        "nombre_completo" -> request.body.asFormUrlEncoded.get("nombre_completo")(0),
        "correo" -> request.body.asFormUrlEncoded.get("correo")(0),
        "ejecutivo_encargado" -> request.body.asFormUrlEncoded.get("ejecutivo_encargado")(0),
        "direccion" -> request.body.asFormUrlEncoded.get("direccion")(0),
        "celular" -> request.body.asFormUrlEncoded.get("celular")(0))
    )

    val selector = Json.obj(
      "tipo_doc" -> request.body.asFormUrlEncoded.get("tipo_doc")(0),
      "documento" -> request.body.asFormUrlEncoded.get("documento")(0)
    )

    for {
      clientes <- clientesFuture
      lastError <- clientes.update(selector,json)
    } yield Ok("Mongo LastError: %s" . format(lastError))

  }


  


//Mezclar con findById para que solo retorne la  informacion
  def findInfoById(documentType:String, documentNumber:String) = Action.async {
    val cursor: Future[List[JsObject]] = clientesFuture.flatMap{ clientes =>
      clientes.find(Json.obj("tipo_doc"->documentType, "documento" -> documentNumber)).
        sort(Json.obj("documento" -> -1)).
        projection(Json.obj(
        "nombre_completo" -> 1,
        "tipo_doc" -> 1,
        "documento" -> 1,
        "correo" -> 1,
        "ejecutivo_encargado" -> 1,
        "direccion" -> 1,
        "celular" -> 1,
        "ubicacion"->1, 
          "_id" -> 0)).
        cursor[JsObject](ReadPreference.primary).collect[List]()
    }

    cursor.map { clientes =>
      Ok(Json.toJson(clientes)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
    }
  }

  def findProductsById(documentType:String, documentNumber:String) = Action.async {
    val cursor: Future[List[JsObject]] = clientesFuture.flatMap{ clientes =>
      clientes.find(Json.obj("tipo_doc"->documentType, "documento" -> documentNumber)).
        sort(Json.obj("documento" -> -1)).
        projection(Json.obj(
          "productos" -> 1,
          "_id" -> 0)).
        cursor[JsObject](ReadPreference.primary).collect[List]()
    }

    cursor.map { clientes =>
      Ok(Json.toJson(clientes)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
    }
  }
*/
}
