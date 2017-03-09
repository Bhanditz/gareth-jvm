package org.craftsmenlabs.gareth.rest

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import okhttp3.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


@Configuration
open class RestClientConfig {

    @Value("\${http.user:}")
    private lateinit var user: String
    @Value("\${http.password:}")
    private lateinit var password: String
    @Value("\${http.url:}")
    private lateinit var url: String

    @Bean
    open fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.findAndRegisterModules()

        val javaTimeModule = JavaTimeModule()
        mapper.registerModule(javaTimeModule)

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        return mapper
    }


    @Bean
    open fun retrofitClient(): Retrofit.Builder {
        val builder = Retrofit.Builder()
        if (user.isNotBlank()) {
            val clientBuilder = OkHttpClient.Builder()
            clientBuilder.authenticator(object : Authenticator {
                override fun authenticate(route: Route, response: Response): Request? {
                    val credential = Credentials.basic(user, password)
                    return response.request().newBuilder().header("Authorization", credential).build()
                }
            })
            builder.client(clientBuilder.build())
        }
        return builder.addConverterFactory(JacksonConverterFactory.create(objectMapper()))
    }

    @Bean
    open fun templateEndpointClient(retrofitBuilder: Retrofit.Builder): ExperimentTemplateEndpointClient {
        return retrofitBuilder.baseUrl(url).build().create(ExperimentTemplateEndpointClient::class.java!!)
    }

    @Bean
    open fun overviewClient(retrofitBuilder: Retrofit.Builder): OverviewEndpointClient {
        return retrofitBuilder.baseUrl(url).build().create(OverviewEndpointClient::class.java!!)
    }

    @Bean
    open fun experimentEndpointClient(retrofitBuilder: Retrofit.Builder): ExperimentEndpointClient {
        return retrofitBuilder.baseUrl(url).build().create(ExperimentEndpointClient::class.java!!)
    }

    @Bean
    open fun gluelineLookupClient(retrofitBuilder: Retrofit.Builder): GluelineLookupEndpointClient {
        return retrofitBuilder.baseUrl(url).build().create(GluelineLookupEndpointClient::class.java!!)
    }


}