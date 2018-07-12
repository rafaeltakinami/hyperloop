package br.com.guiabolso

import br.com.guiabolso.config.s3BucketName
import br.com.guiabolso.schemas.S3SchemaRepository
import br.com.guiabolso.schemas.SchemaKey
import com.amazonaws.regions.Regions

// TODO - REMOVER ESTA CLASSE
fun main(args: Array<String>) {

//    val s3Client = AmazonS3ClientBuilder
//            .standard()
//            .withRegion("sa-east-1")
//            .build()

//    val s3Object = s3Client.getObject("beedrill-videos", "consumo-consciente-evelin.mp4")
//    val repository = S3SchemaRepository("lumberjack-prod/alexandre/2017-09-07/07731a72-c72a-42e9-a23a-b59b937f9ea6", Regions.US_EAST_1)
//    repository.get("request.csv")
//    val schemaKey = SchemaKey("insecure:test", 1)
    val scriptStore = S3SchemaRepository(s3BucketName(), Regions.US_EAST_1)

    val schemaKey = SchemaKey.parse("insecure:test_V1.yaml")


    scriptStore.get(schemaKey)
}