package net.nhs.esb.transfer.route;

import org.apache.camel.ExchangePattern;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

/**
 */
@Component
public class DeletePatientTransferRouteBuilder extends SpringRouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:deletePatientTransferComposition").routeId("openEhrDeletePatientTransferComposition")
                .to("direct:setHeaders")
                .to("direct:createSession")
                .to("direct:getEhrId")
                .setExchangePattern(ExchangePattern.InOut)
                .setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, constant(Boolean.FALSE))
                .setHeader(CxfConstants.OPERATION_NAME, constant("deleteComposition"))
                .setBody(simple("${header.compositionId}"))
                .to("cxfrs:bean:rsOpenEhr")
                .end();
    }
}
