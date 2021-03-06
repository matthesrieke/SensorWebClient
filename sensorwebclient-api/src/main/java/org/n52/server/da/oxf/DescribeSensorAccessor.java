
package org.n52.server.da.oxf;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_OUTPUT_FORMAT;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.DESCRIBE_SENSOR;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescribeSensorAccessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DescribeSensorAccessor.class);

    /**
     * Requests the sensor description from an SOS instance. The sensor description is requested with respect
     * to the {@link SOSMetadata#getVersion()} and {@link SOSMetadata#getSensorMLVersion()} set in the passed
     * service metadata.<br>
     * <br>
     * Currently, only SensorML 1.0.1 is supported.
     * 
     * @param procedure
     *        the procedure id for which the sensor description shall be requested.
     * @param serviceMetadata
     *        the SOS service metadata.
     * @return the sensor description SensorML as XML.
     */
    public static XmlObject getSensorDescriptionAsSensorML(String procedure, SOSMetadata serviceMetadata) {
        String serviceUrl = serviceMetadata.getServiceUrl();
        String sosVersion = serviceMetadata.getSosVersion();
        String smlVersion = serviceMetadata.getSensorMLVersion();

        try {
            ParameterContainer parameters = new ParameterContainer();
            parameters.addParameterShell(DESCRIBE_SENSOR_SERVICE_PARAMETER, "SOS");
            parameters.addParameterShell(DESCRIBE_SENSOR_VERSION_PARAMETER, sosVersion);
            parameters.addParameterShell(DESCRIBE_SENSOR_PROCEDURE_PARAMETER, procedure);
            if (SosUtil.isVersion100(sosVersion)) {
                parameters.addParameterShell(DESCRIBE_SENSOR_OUTPUT_FORMAT, smlVersion);
            }
            else if (SosUtil.isVersion200(sosVersion)) {
                parameters.addParameterShell(DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT, smlVersion);
            }
            else {
                throw new IllegalStateException("SOS Version (" + sosVersion + ") is not supported!");
            }

            Operation describeSensor = new Operation(DESCRIBE_SENSOR, serviceUrl, serviceUrl);
            SOSAdapter adapter = SosAdapterFactory.createSosAdapter(serviceMetadata);

            OperationAccessor accessor = new OperationAccessor(adapter, describeSensor, parameters);
            FutureTask<OperationResult> task = new FutureTask<OperationResult>(accessor);
            AccessorThreadPool.execute(task);

            OperationResult result = task.get(SERVER_TIMEOUT, MILLISECONDS);

            // TODO check for different SML versions

            return XmlObject.Factory.parse(result.getIncomingResultAsStream());
        }
        catch (OXFException e) {
            LOGGER.warn("Could not assemble parameters for request.", e);
        }
        catch (InterruptedException e) {
            LOGGER.warn("Interrupted while sending request.", e);
        }
        catch (ExecutionException e) {
            LOGGER.warn("Could not execute DescribeSensor request with '{}' to '{}'.", procedure, serviceUrl, e);
        }
        catch (TimeoutException e) {
            LOGGER.warn("Timeout when sending request.", e);
        }
        catch (IOException e) {
            LOGGER.warn("Could not read sensor description.", e);
        }
        catch (XmlException e) {
            LOGGER.warn("Could not parse sensor description.", e);
        }

        // TODO check for different SML versions

        // an error occured ...
        LOGGER.warn("Failed to retrieve sensor description for '{}'. Return an empty description.", procedure);
        return SensorMLDocument.Factory.newInstance();
    }
}
