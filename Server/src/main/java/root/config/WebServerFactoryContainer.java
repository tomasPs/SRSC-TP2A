package root.config;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
class WebServerFactoryContainer implements
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory
            .addConnectorCustomizers((connector) ->
            {
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler())
                    .setUseServerCipherSuitesOrder(true);
            });
    }
}

