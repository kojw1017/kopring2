package hello.kopring2

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@ComponentScan(excludeFilters = ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration::class))
class AutoAppConfig {

}