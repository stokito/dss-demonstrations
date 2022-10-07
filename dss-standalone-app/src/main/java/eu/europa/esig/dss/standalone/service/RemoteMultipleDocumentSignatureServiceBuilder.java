package eu.europa.esig.dss.standalone.service;

import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.asic.cades.signature.ASiCWithCAdESService;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.jades.signature.JAdESService;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.spi.x509.aia.OnlineAIASource;
import eu.europa.esig.dss.standalone.source.TSPSourceLoader;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.ws.signature.common.RemoteMultipleDocumentsSignatureServiceImpl;
import eu.europa.esig.dss.xades.signature.XAdESService;

public class RemoteMultipleDocumentSignatureServiceBuilder {

    private TrustedListsCertificateSource tslCertificateSource = new TrustedListsCertificateSource();

    public void setTslCertificateSource(TrustedListsCertificateSource tslCertificateSource) {
        this.tslCertificateSource = tslCertificateSource;
    }

    public RemoteMultipleDocumentsSignatureServiceImpl build() {
        RemoteMultipleDocumentsSignatureServiceImpl service = new RemoteMultipleDocumentsSignatureServiceImpl();
        service.setAsicWithCAdESService(asicWithCadesService());
        service.setAsicWithXAdESService(asicWithXadesService());
        service.setXadesService(xadesService());
        service.setJadesService(jadesService());
        return service;
    }

    private CommonsDataLoader dataLoader() {
        CommonsDataLoader dataLoader = new CommonsDataLoader();
        dataLoader.setProxyConfig(proxyConfig());
        return dataLoader;
    }

    private OnlineAIASource onlineAIASource() {
        OnlineAIASource onlineAIASource = new DefaultAIASource();
        onlineAIASource.setDataLoader(dataLoader());
        return onlineAIASource;
    }

    private OnlineCRLSource onlineCRLSource() {
        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(dataLoader());
        return onlineCRLSource;
    }

    private OCSPDataLoader ocspDataLoader() {
        OCSPDataLoader ocspDataLoader = new OCSPDataLoader();
        ocspDataLoader.setProxyConfig(proxyConfig());
        return ocspDataLoader;
    }

    private OnlineOCSPSource onlineOCSPSource() {
        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        onlineOCSPSource.setDataLoader(ocspDataLoader());
        return onlineOCSPSource;
    }

    private ProxyConfig proxyConfig() {
        // not defined by default
        return null;
    }

    private CertificateVerifier certificateVerifier() {
        CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();
        certificateVerifier.setCrlSource(onlineCRLSource());
        certificateVerifier.setOcspSource(onlineOCSPSource());
        certificateVerifier.setAIASource(onlineAIASource());
        certificateVerifier.setTrustedCertSources(tslCertificateSource);

        // Default configs
        certificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
        certificateVerifier.setCheckRevocationForUntrustedChains(false);

        return certificateVerifier;
    }

    private ASiCWithCAdESService asicWithCadesService() {
        ASiCWithCAdESService service = new ASiCWithCAdESService(certificateVerifier());
        service.setTspSource(TSPSourceLoader.getTspSource());
        return service;
    }

    private ASiCWithXAdESService asicWithXadesService() {
        ASiCWithXAdESService service = new ASiCWithXAdESService(certificateVerifier());
        service.setTspSource(TSPSourceLoader.getTspSource());
        return service;
    }

    private XAdESService xadesService() {
        XAdESService service = new XAdESService(certificateVerifier());
        service.setTspSource(TSPSourceLoader.getTspSource());
        return service;
    }

    private JAdESService jadesService() {
        JAdESService service = new JAdESService(certificateVerifier());
        service.setTspSource(TSPSourceLoader.getTspSource());
        return service;
    }

}
