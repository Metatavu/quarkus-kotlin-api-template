package fi.metatavu.example.api.test.functional.tests

import fi.metatavu.example.api.test.functional.resources.LocalTestProfile
import fi.metatavu.example.api.test.functional.resources.MysqlTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.NativeImageTest
import io.quarkus.test.junit.TestProfile
/**
 * Native tests for Examples
 *
 * @author Jari Nykänen
 * @author Antti Leppä
 */
@NativeImageTest
@QuarkusTestResource.List(
    QuarkusTestResource(MysqlTestResource::class)
)
@TestProfile(LocalTestProfile::class)
class NativePagesTestIT: ExamplesTestIT() {

}
