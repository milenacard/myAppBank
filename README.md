# Internal API MyBank

## Modo de Uso

Las llamadas al api deben contener el token que provee firebase ya sea como HTTP Header (preferido)

HTTP Authorization Bearer \[TOKEN\]

o como parametro en la ruta

http://mybankinternal.herokuapp.com/\[RUTA_A_CONSUMIR\]?token=\[TOKEN\]

## Rutas habilitadas: Clientes

### GET /clientes
Ej. (http://mybankinternal.herokuapp.com/clientes)

Retorna un array con la informacion de todos los clientes y con la siguiente estructura

```
[
  {
    "_id": {
      "$oid": "572973263fdca6fc11e8590a"
    },
    "nombre_completo": "Janice Greene",
    "tipo_doc": "ce",
    "documento": "931547116",
    "ejecutivo_encargado": "BBVACEO",
    "correo": "jgreenes@cafepress.com",
    "productos": [
      {
        "nombre": "Credito de libre inversion",
        "tipo": "Prestamo",
        "saldo": 6493947
      },
      {
        "nombre": "Poliza de seguros contra accidentes laborales",
        "tipo": "Seguro",
        "saldo": 9172070
      },
      {
        "nombre": "Poliza de seguro de vida",
        "tipo": "Seguro",
        "saldo": 70984
      },
      {
        "nombre": "Deposito a termino fijo - 60 dias",
        "tipo": "CDT",
        "saldo": 7900067
      }
    ]
  }
  ....
]
```

### GET /clientes/\[tipoDocumento\]/\[NumeroDocumento\]?token=\[TOKEN\]
Ej. (http://mybankinternal.herokuapp.com/clientes/adultonn/1037008984?token=TOKEN)

Retorna un array de un solo objeto con toda la informacion del cliente que concuerde con el tipo de documento y el numero de identificacion, se valida el token de firebase

```
[
  {
    "_id": {
      "$oid": "572973263fdca6fc11e8590a"
    },
    "nombre_completo": "Janice Greene",
    "tipo_doc": "ce",
    "documento": "931547116",
    "ejecutivo_encargado": "BBVACEO",
    "correo": "jgreenes@cafepress.com",
    "productos": [
      {
        "nombre": "Credito de libre inversion",
        "tipo": "Prestamo",
        "saldo": 6493947
      },
      {
        "nombre": "Poliza de seguros contra accidentes laborales",
        "tipo": "Seguro",
        "saldo": 9172070
      },
      {
        "nombre": "Poliza de seguro de vida",
        "tipo": "Seguro",
        "saldo": 70984
      },
      {
        "nombre": "Deposito a termino fijo - 60 dias",
        "tipo": "CDT",
        "saldo": 7900067
      }
    ]
  }
]
```
### GET /clientes/\[tipoDocumento\]/\[NumeroDocumento\]/productos

Retorna los productos de un cliente

### GET /clientes/\[tipoDocumento\]/\[NumeroDocumento\]/info

Retorna la información personal de un cliente

### POST /clientes/update

Actualiza los datos de un cliente

Acepta los parametros

```
documento: String (Requerido)
tipo_doc: String (Requerido)
ejecutivo_encargado: String (Requerido)
nombre_completo: String (Requerido)
correo: String (Requerido)
```

### POST /clientes/post

Acepta los parametros

```
documento: String (Requerido)
tipo_doc: String (Requerido)
ejecutivo_encargado: String (Requerido)
nombre_completo: String (Requerido)
correo: String (Requerido)
```
---

## Rutas habilitadas: Ejecutivos

### Exactamente igual a la de clientes. La url cambia en vez de `clientes`se usa `ejecutivos`
Ej. (http://mybankinternal.herokuapp.com/ejecutivos/adultonn/1037008984?token=TOKEN)


