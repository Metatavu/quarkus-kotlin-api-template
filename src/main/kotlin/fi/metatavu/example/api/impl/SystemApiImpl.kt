package fi.metatavu.example.api.impl

import fi.metatavu.example.spec.SystemApi
import javax.enterprise.context.RequestScoped
import javax.ws.rs.core.Response

/**
 * System API implementation
 *
 * @author Antti Leppä
 */
@RequestScoped
class SystemApiImpl: SystemApi, AbstractApi()  {

    override suspend fun ping(): Response {
        return createOk("pong")
    }

}
