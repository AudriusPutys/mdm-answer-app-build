package uk.ac.ox.softeng.maurodatamapper.application.boot

import uk.ac.ox.softeng.maurodatamapper.application.Application

import grails.boot.config.GrailsApplicationPostProcessor
import grails.core.DefaultGrailsApplication
import grails.core.GrailsApplicationLifeCycle
import grails.io.IOUtils
import groovy.util.logging.Slf4j
import org.grails.config.PropertySourcesConfig
import org.grails.config.yaml.YamlPropertySourceLoader
import org.springframework.boot.env.PropertySourceLoader
import org.springframework.context.ApplicationContext
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.PropertySource
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource

import java.nio.file.Path

/**
 * @since 14/11/2017
 */
@Slf4j
class RuntimeGrailsApplicationPostProcessor extends GrailsApplicationPostProcessor {

    public static final String APPLICATION_CONFIGURATION_PROPERTIES_PROPERTY_SOURCE = 'applicationConfig: [classpath:/application.yml]'
    public static final String RUNTIME_CONFIGURATION_PROPERTIES_PROPERTY_SOURCE = 'runtimeConfiguration'
    public static final String BUILD_CONFIGURATION_PROPERTIES_PROPERTY_SOURCE = 'buildConfiguration'

    RuntimeGrailsApplicationPostProcessor(GrailsApplicationLifeCycle lifeCycle,
                                          ApplicationContext applicationContext, Class... classes) {
        super(lifeCycle, applicationContext, classes)
    }

    @Override
    protected void loadApplicationConfig() {
        super.loadApplicationConfig()

        PropertySourcesConfig config = ((DefaultGrailsApplication) grailsApplication).config
        MutablePropertySources propertySources = config.getPropertySources()

        try {
            PropertySource propertySource = loadPropertySourceForResource(BUILD_CONFIGURATION_PROPERTIES_PROPERTY_SOURCE, '/build.yml')
            addPropertySourceToPropertySources(propertySource, APPLICATION_CONFIGURATION_PROPERTIES_PROPERTY_SOURCE, config, propertySources)
        } catch (Exception ignored) {
            log.warn("Build configuration file was not loaded", ignored)
        }

        String filePath = System.getenv('runtime.config.path')
        if(!filePath){
            log.info("Runtime configuration file was not supplied")
            return
        }

        try {
            PropertySource propertySource = loadPropertySourceForFilePath(RUNTIME_CONFIGURATION_PROPERTIES_PROPERTY_SOURCE, filePath)
            String placement = propertySources.contains(BUILD_CONFIGURATION_PROPERTIES_PROPERTY_SOURCE) ? BUILD_CONFIGURATION_PROPERTIES_PROPERTY_SOURCE :
                               APPLICATION_CONFIGURATION_PROPERTIES_PROPERTY_SOURCE
            addPropertySourceToPropertySources(propertySource, placement, config, propertySources)
        } catch (Exception ignored) {
            log.warn("Runtime configuration file was not loaded", ignored)
        }
    }


    void addPropertySourceToPropertySources(PropertySource propertySource, String placement, PropertySourcesConfig config, MutablePropertySources propertySources) {
        if (propertySource) {
            propertySources.addBefore placement, propertySource
            config.refresh()
            log.info('Updated property sources to include properties from {} configuration file', propertySource.getName())
        }
    }

    PropertySource loadPropertySourceForResource(String propertySourceName, String fileName) {
        URL urlToConfig = IOUtils.findResourceRelativeToClass(Application, fileName)
        loadPropertySourceForUrl(propertySourceName, urlToConfig)
    }

    PropertySource loadPropertySourceForFilePath(String propertySourceName, String filePath) {
        URI uriToConfig = Path.of(filePath).toAbsolutePath().toUri()
        loadPropertySourceForUrl(propertySourceName, uriToConfig.toURL())
    }

    PropertySource loadPropertySourceForUrl(String propertySourceName, URL uriToConfig) {
        try {
            Resource resource = new UrlResource(uriToConfig)
            PropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader()
            return propertySourceLoader.load(propertySourceName, resource).first()
        }
        catch (Exception ignored) {
        }
        log.warn("Configuration file for [{}] not present at [{}]", propertySourceName, uriToConfig)
        null
    }
}