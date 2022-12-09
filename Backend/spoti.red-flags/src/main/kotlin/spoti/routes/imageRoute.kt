package spoti.routes

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.ListObjectsRequest
import aws.smithy.kotlin.runtime.content.toByteArray
import aws.smithy.kotlin.runtime.content.writeToFile

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.koin.ktor.ext.inject
import spoti.data.model.ImageStoreData
import java.io.File
import kotlin.text.get

fun Route.imageRoute(){
    val s3 : S3Client by inject()
    route("/api/catImage/{id}"){
        get {
            val id = call.parameters["id"]
            val request = GetObjectRequest {
                key= "${id}.jpg"
                bucket = "redflag-cats"
            }
            var imgBytes : ByteArray= ByteArray(0)
            s3.getObject(request){
                resp ->
                if(resp.body!=null)
                    imgBytes= resp.body!!.toByteArray()
            }
            call.respondBytes(imgBytes)
        }
    }

    route("/api/images"){
        get {
            val request = ListObjectsRequest {
                bucket = "redflag-cats"
            }
            val count= s3.listObjects(request).contents?.count()
            call.respond(HttpStatusCode.OK, ImageStoreData(count?: 0))
        }
    }
}