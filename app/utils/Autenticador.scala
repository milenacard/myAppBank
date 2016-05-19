package utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import java.io.UnsupportedEncodingException
import java.util.Date
import java.util.logging.Level
import java.util.logging.Logger
//remove if not needed
import scala.collection.JavaConversions._

class Autenticador {

  private val FB_SECRET = "Iz0jgNOaHJyBeQaQdQgU95UctzutLhzKXg8sUf0g"

  private val Personal_SECRET = "Zf7OVWIFJlfChJXcNKR6orBdDMhxEp1NYpdjNXlV"

  def validarToken(token: String): Boolean = {
    try {
      val claims = Jwts.parser().setSigningKey(FB_SECRET.getBytes("UTF-8"))
        .parseClaimsJws(token)
        .getBody
      val issuedDate = claims.getIssuedAt
      println("issued date: " + claims.getIssuedAt.toString)
      println("Subject: " + claims.getSubject)
      println("ID: " + claims.getId)
      println("Isuer: " + claims.getIssuer)
      val hechoAntesDeAhora = issuedDate.before(new Date())
      if (hechoAntesDeAhora) {
        return true
      } else {
        println("Mierda nos ingresaron un token elaborado en el futuro!!!!")
        return false
      }
    } catch {
      case e: io.jsonwebtoken.SignatureException => println("este no es el secreto pa ese token")
      case ex: UnsupportedEncodingException => println("codificacion erronea")
      case ex: io.jsonwebtoken.MalformedJwtException => println("codificacion erronea")
      case ex: NullPointerException => println("lamento decirte que no es valido(token nulo o falta la fecha)")
    }
    false
  }
}
