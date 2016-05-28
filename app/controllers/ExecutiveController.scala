package controllers

import javax.inject.{Inject, Singleton}

import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo._
import reactivemongo.api.ReadPreference
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}
import utils.Autenticador
import play.api.Logger

/**
  * Created by PE702QE on 01/05/2016.
  */
@Singleton
class ExecutiveController @Inject()(val reactiveMongoApi: ReactiveMongoApi)(implicit exec: ExecutionContext) extends Controller with MongoController with ReactiveMongoComponents  {
  def ejecutivosFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection]("Ejecutivos"))

  def find = Action.async {
    val cursor: Future[List[JsObject]] = ejecutivosFuture.flatMap{ ejecutivos =>
      ejecutivos.find(Json.obj())
        .sort(Json.obj("documento" -> -1))
        .cursor[JsObject](ReadPreference.primary).collect[List]()
    }
    cursor.map { ejecutivos =>
      Ok(Json.toJson(ejecutivos)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
    }
  }

  def findById(documentType:String, documentNumber:String,token:String) = Action.async {
     val auth = new Autenticador
        val cursor: Future[List[JsObject]] = ejecutivosFuture.flatMap{ ejecutivos =>
          ejecutivos.find(Json.obj("tipo_doc"->documentType, "documento" -> documentNumber)).
            sort(Json.obj("documento" -> -1)).
            cursor[JsObject](ReadPreference.primary).collect[List]()
        }
        if(auth.validarToken(token)){Logger.info("Valid Token")
            cursor.map { ejecutivos =>
              Ok(Json.toJson(ejecutivos)).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")
            }
        }else{
                Logger.info("Invalid token")
                cursor.map { ejecutivos =>
              Ok(Json.obj("err"->"Invalid Token")).withHeaders(ACCESS_CONTROL_ALLOW_ORIGIN -> "*")}
            
            }
  }
   def create = Action.async { request =>

      val json = Json.obj(
        "id_ejecutivo" -> request.body.asFormUrlEncoded.get("id_ejecutivo")(0),
        "nombre" -> request.body.asFormUrlEncoded.get("nombre")(0),
        "foto" -> request.body.asFormUrlEncoded.get("foto")(0),
        "tipo_doc" -> request.body.asFormUrlEncoded.get("tipo_doc")(0),
        "documento" -> request.body.asFormUrlEncoded.get("documento")(0),
        "correo" ->request.body.asFormUrlEncoded.get("correo")(0),
        "celular" -> request.body.asFormUrlEncoded.get("celular")(0),
        "direccion" -> request.body.asFormUrlEncoded.get("direccion")(0),
        "ubicacion" -> Json.obj("latitud" -> request.body.asFormUrlEncoded.get("latitud")(0) , "longitud"-> request.body.asFormUrlEncoded.get("longitud")(0))
      )

      for {
        ejecutivos <- ejecutivosFuture
        lastError <- ejecutivos.insert(json)
      } yield Ok("Post status: OK. Inserted as: %s" . format(lastError))

  }
  
  def update = Action.async { request =>

    val json = Json.obj(
      "$set" -> Json.obj(
        "id_ejecutivo" -> request.body.asFormUrlEncoded.get("id_ejecutivo")(0),
        "nombre" -> request.body.asFormUrlEncoded.get("nombre")(0),
        "foto" -> request.body.asFormUrlEncoded.get("foto")(0),
        "correo" ->request.body.asFormUrlEncoded.get("correo")(0),
        "celular" -> request.body.asFormUrlEncoded.get("celular")(0),
        "direccion" -> request.body.asFormUrlEncoded.get("direccion")(0),
        "ubicacion" -> Json.obj("latitud" -> request.body.asFormUrlEncoded.get("latitud")(0) , "longitud"-> request.body.asFormUrlEncoded.get("longitud")(0))
        )
    )

    val selector = Json.obj(
      "tipo_doc" -> request.body.asFormUrlEncoded.get("tipo_doc")(0),
      "documento" -> request.body.asFormUrlEncoded.get("documento")(0)
    )

    for {
      ejecutivos <- ejecutivosFuture
      lastError <- ejecutivos.update(selector,json)
    } yield Ok("Post status: OK. Inserted as: %s "  format(lastError))

  }  
  
}
