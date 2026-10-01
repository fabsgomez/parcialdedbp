utec desea centralizar la difusion y gestion de actividades academicas, culturales y deportivas, se requierdesarollar EVentPaSSUTEC, una api rest que permita administrar eventos, tipos de entrada e inscripcionesde paritcipantes, la imprementacion debe aplicar arquitectura de contoller service repository, dtos, springdatajpa,validacion, manejo global de excepciones,transcciones,ecentos,seguridadjwt

Entidades
el sistema necesita manejar los siguientes objetos de dominio
User
id: long (autogenerado)
username: String (unico, obligatorio)
email: string (unico, obligatorio)
password: string (obligatorio, encriptado)
role: String (ROLE_ATTENDEE, ROLE_ORGANIZER, ROLE_ADMIN)

    campusevent
    id: long (autogenerado)
    organizerID: long (referencia a user)
    title: String (obligatorio)
    description: String (maximo 500 caracteres)
    category: String (ACADEMIC, CULTURAL,SPORTS,TECNOLOGY)
    eventDate:ZonedDAteTIMe
    location:String
    status: String (DRAFT, PUBLISHED, CANCELLED, FINISHED)

    TicketType
    //id: Long (autogenerado)
    //eventID: Long (referencia a CampusEvent)
    //name: String (obligatorio)
    //capacity: Integer (>=1)
    //registeredCount: Integer (default: 0)
    //status: String(AVAILABLE, FULL, INACTIVE)

    //EventRegistration
    //id: Long(autogenerado)
    //eventId: Long (referencia a CampusEvent)
    //ticketTypeId: Long (referencia a TicketType)
    //attendeedId: Long (referencia a User)
    //registeredAt: ZonedDateTime
    //status: String (CONFIRMED,CHECKED_IN, CANCELLED)




//Endpoints

Registro de Usario: POST/auth/register
//Request DTO
{
"username": "sofia.event",
"email": "sofia@utec.edu.pe"
"password": "Event2026A"
}
//Responde DTO
{
"id": "1",
"username": "sofia.event"
"email": "sofia@utec.edu.pe"
}

Status Code : 201 //Created

Validaciones:
{"username y email deben de ser unicos"}
{"password debe tener al menos 8 caracteres"}
{"email debe de tener un formato valido"}

Logica:

        {"La contraseña debe almacenar usando PasswordEncoder"}
        {"El rol inicial es ROLE_USER"}
        {"Autenticación: POST/auth/login"}
//Request DTO
{
"username": "sofia.event",
"password": "Event2026A"
}
//Responde DTO
{
"token": "eyJhbGciOiJIUzI1NiIs",
"expiresIn": "3600"
}

Status Code : 200 //ok

Validaciones:
{"username y password son obligatorios"}

Logica:

        {"Las credenciales válidas generan un token JWT"}
        {"Las credenicales invalidas devuelven 401 Unauthorized"}

CrearEvento: POST/events

//Request DTO
{
"title": "Ferua de Proyectos CS",
"description": "Presentacion de proyectos estudiantiles"
"category":"TECHNOLOGY"
"eventDate : "2026-10-20T16:00:00-05:00 "
"location" : "Auditorio UTEC"
}
//Responde DTO
{
"id": "14",
"organizerUsername": "sofia.event",
"title": "Feria de Proyectos CS",
"category" : "TECNOLOGY",
"status" : "DRAFT"
}
Headers:
//autorization: Bearer {{token}}
//Content-Type: application/json
Status Code : 201 //created

Validaciones:
{"solo ROLE_ORGANIZER o ROLE_ADMIN pueden crear eventos"}
{"title debe tener entre 5 y 120 caracteres"}
{"eventDAte debe ser futura"}
{"location es obligatoria"}

Logica:

        {"organizerId se obtiene del JWT"}
        {"status se incializa en DRAFT"}

Publicar evento: PATCH/events/{eventID}/publish
//Request DTO
{
"id": 14,
"title":"Feria de proyecto CS"
"status : "Published "
}

Header:
Authorization: Bearer {{token}}
Status code: 200 OK

Validaciones:
"el evento debe existir y pertenecer al organizador autenticado"
"debe tener al menos un TcketType activo"
"solo puede publicarse desde DRAFT"

Logica
//La verificacion de propiedad puede implementarese en el servide o mediante @preauthorize

Buscar eventos: GET/events

//Responde DTO
{
"content":({
"id":14, "title":"Feria de Proyectos CS",
"category" :"TECNOLOGY",
"event DATE" :"2023-10-20T16:00:00-05:00"
"availableSLots" :"80"
})
"page" : 0 , "size": 10, "totalElements": 9
}

Status Code : 200 //ok

queryparameters:
category: all / academic/cultural/sports / tecnology
from: ZonedDAteTime (opcional)
PAge: 0 , size: 10

logica
mostrar unicamentente eventos PUBLISHED y futuros
availableSlots es la suma de capacity - registeredCount de entradas activas

Ordenar por eventDAte ascnedente
Inscribirse en evento: POST/events/{eventID}/registrations

//Request DTO
{
"ticketTypeID":3,
}

//Response DTO
{
"id": 38, "eventID": 14,
"eventTitle" : "Feria de Proyectos CS",
"ticketType" : "entrada general",
"attendeeUsername". "ana.student" ,
"status" : "confirmed"
}
headers:
Authorization: Bearer {{token}}
Status Code: 201 created

Validaciones:
{"El evento debe estar PUBLISHED y ser futuro"}
{"el tipo de entrada debe pertenecer al evento y tener cupos"}
{"el usuario no debe estar inscrito previamente"}

logica

"attendeddID se obtiene del JWT"
"inscripicion e incremento de registeredCount son atomicos"
"si registeredCount alcanza Capacity, el tickettype cambia a full"
"despues del commit se publica RegistationconfirmedEvent"

Consutlar mis inscripcion: GET/my-event-registrations

//Responde DTo
{
"content": ({
"id": 38, "eventtitle": "feria de proyectos CS",
"status" : "confirmed"
})
"page": 0 "size": 10, "totalElements": 2
}

Headers:
Autgorization: Bearer {{token}}
status code: 200 Ok
Query parameters:
Status: All / confirmed/checked_in/cancelled
page: 0 , size:10

logica
//retornar solamente inscripciones el usuario autenticado
//ordenar por registeredAt descendte


REquisitos transversales

"USar DTo de entrada y salida con bean validation ; no exponer entidades JPA directamente, proteger las operaciones privadas con JWT y aplicar autorizacion por rol y propiedad del evento." +
"Definir la inscripion y actualizacion de cupos dentro de una unica transaccion"
"Procesar la generacion de entradas y notificaciones mediante eventos AFTER_COMMIT y listeners asincronicos"



        Pruebas minimas
        "Prueba unitaria de registationService para inscripcion exitosa , evento no publicado y entrada sin cupos" +
                "@datajpatest para verificar la busqueda de eventos futuros y la restriccion de inscripcion unica" +
                "prueba de integracion del flujo registro login e inscripcion utlizando jwt y postregreSQL cin testcontainer"







    //La imprementacion debe contempral las sigueintes excepciones
    UserAlreadyExistsEXception = 409 COnflict
    Caso: POST/auth/register con username o email registrad
    invalidcredetialsexception = 401
    Caso: POST/auth/login con credenciales incorrectas
    EventNotFOundException = 404 not found
    Caso: eventIdinexistente
    tickettypenotfoundexception= 404 not found
    Caso: ticketTypeId inexistente o perteneciente a otro evento
    tickettypefullexception = 409 COnflict
    Caso: PregisterCount >= capacity
    Alreadyregisteredexception = 409 COnflict
    Caso: el usuario ya est ainscrito en el evento
    ForbiddenEventAction exeption = 403 forbidden
    caso: rol insuficiente o modificaciond e un evento ajeno

