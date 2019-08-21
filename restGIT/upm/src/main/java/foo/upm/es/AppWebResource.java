/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package foo.upm.es;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onlab.osgi.DefaultServiceDirectory;
import org.onlab.util.Bandwidth;
import org.onosproject.net.*;
import org.onosproject.net.behaviour.*;
import org.onosproject.net.device.DefaultPortDescription;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortDescription;
import org.onosproject.rest.AbstractWebResource;
import org.slf4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.onlab.util.Tools.readTreeFromStream;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Sample web resource.
 */
@Path("queues")
public class AppWebResource extends AbstractWebResource {
    private static final String QOS = "qos";
    private final Logger log = getLogger(getClass());
    /**
     * Get hello world greeting.
     *
     * @return 200 OK
     */
    @POST
    @Path("{deviceId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addQueue(@PathParam("deviceId") String deviceId,
                            InputStream stream)  {
        ObjectNode root = mapper().createObjectNode();




        String portName = "";
        String portNumber = "";
        String portSpeed = "";
        String name = "";
        String minRate = "";
        String maxRate = "";
        String burst = "";
        String type = "add";

        ObjectNode jsonTree = null;
        try {
            jsonTree = readTreeFromStream(mapper(), stream);
            portName = (String)jsonTree.get("portName").asText();
            portNumber = (String)jsonTree.get("portNumber").asText();
            portSpeed = (String)jsonTree.get("portSpeed").asText();
            name = (String)jsonTree.get("name").asText();
            minRate = (String)jsonTree.get("minRate").asText();
            maxRate = (String)jsonTree.get("maxRate").asText();
            burst = (String)jsonTree.get("burst").asText();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("IOException");
        }
        
        log.info(portName);
        log.info(portNumber);
        log.info(portSpeed);
        log.info(name);
        log.info(minRate);
        log.info(maxRate);
        log.info(burst);

        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(deviceId));

        if (device == null) {
            log.info("{} isn't support config.", deviceId);
            return Response.ok(root).build();
        }
        else {
            try{
                log.info("{} supports config.", deviceId);
                QueueDescription queueDesc = DefaultQueueDescription.builder()
                        .queueId(QueueId.queueId(name))
                        .maxRate(Bandwidth.kbps(Long.parseLong(maxRate)))
                        .minRate(Bandwidth.kbps(Long.valueOf(minRate)))
                        .burst(Long.valueOf(burst+"000"))
                        .build();
                log.info("queueDesc ok");

                //PortDescription portDesc = new DefaultPortDescription(PortNumber.portNumber(Long.valueOf(portNumber), name), true);

                PortDescription portDesc = DefaultPortDescription.builder()
                        .withPortNumber(PortNumber.portNumber(Long.valueOf(portNumber), portName))
                        .portSpeed(Long.valueOf(portSpeed))
                        .isEnabled(true)
                        .isRemoved(false)
                        .type(Port.Type.COPPER)
                        .build();

                log.info("portDesc ok");
                Map<Long, QueueDescription> queues = new HashMap<>();
                queues.put(0L, queueDesc);

                QosDescription qosDesc = DefaultQosDescription.builder()
                        .qosId(QosId.qosId(name))
                        .type(QosDescription.Type.HTB)
                        .maxRate(Bandwidth.kbps(Long.valueOf(maxRate)))
                        .queues(queues)
                        .build();

                log.info("qosDesc ok");
                QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
                QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
                PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);

                log.info("configs ok");
                //ACTION delete add or display
                if (type.equals("add")) {
                    log.info("trying to add");
                    queueConfig.addQueue(queueDesc);
                    log.info("queue added");
                    
                    qosConfig.addQoS(qosDesc);
                    log.info("qos added");
                    
                    //portConfig.applyQoS(portDesc, qosDesc);
                    log.info("port added");

                } else if (type.equals("del")) {
                    queueConfig.deleteQueue(queueDesc.queueId());
                    qosConfig.deleteQoS(qosDesc.qosId());
                    portConfig.removeQoS(portDesc.portNumber());

                } else if (type.equals("display")) {
                    queueConfig.getQueues().stream().forEach(q -> {
                        log.info("name=%s, type=%s, dscp=%s, maxRate=%s, " +
                                        "minRate=%s, pri=%s, burst=%s", q.queueId(), q.type(),
                                q.dscp(), q.maxRate(), q.minRate(),
                                q.priority(), q.burst());
                    });
                    qosConfig.getQoses().forEach(q -> {
                        log.info("name=%s, maxRate=%s, cbs=%s, cir=%s, " +
                                        "queues=%s, type=%s", q.qosId(), q.maxRate(),
                                q.cbs(), q.cir(), q.queues(), q.type());
                    });
                }
            }
            catch(Exception e){
                e.printStackTrace();
                log.info(e.getMessage());
                log.info("ERROR");
                return ok(root).build();
            }

        }


        return ok(root).build();
    
    }

    /**
     * Gets all flow entries. Returns array of all flow rules in the system.
     *
     * @return 200 OK with a collection of flows
     * @onos.rsModel FlowEntries
     */
    @GET
    @Path("{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQueues(@PathParam("deviceId") String deviceId) {
        log.info(deviceId);
        ObjectNode root = mapper().createObjectNode();
        ArrayNode queueNode = root.putArray(QOS);
        log.info(deviceId);
        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(deviceId));

        if(device ==null)
            log.info("device null");
        else log.info("device not null");
        QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
        log.info("queue config ok");
        QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
        log.info("classes qos loaded");
        queueConfig.getQueues().stream().forEach(q -> {
            log.info("name={}, type={}, dscp={}, maxRate={}, " +
                            "minRate={}, pri={}, burst={}", q.queueId(), q.type(),
                    q.dscp(), q.maxRate(), q.minRate(),
                    q.priority(), q.burst());
        });
        qosConfig.getQoses().forEach(q -> {
            queueNode.add(q.queues().toString());
            log.info("name={}, maxRate={}, cbs={}, cir={}, " +
                            "queues={}, type={}", q.qosId(), q.maxRate(),
                    q.cbs(), q.cir(), q.queues(), q.type());

        });

        return ok(root).build();
    }

    /**
     * Get hello world greeting.
     *
     * @return 200 OK
     */
    @POST
    @Path("delete/{deviceId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteQueue(@PathParam("deviceId") String deviceId,
                            InputStream stream)  {
        ObjectNode root = mapper().createObjectNode();

        String portName = "";
        String portNumber = "";
        String portSpeed = "";
        String name = "";
        String minRate = "";
        String maxRate = "";
        String burst = "";

        ObjectNode jsonTree = null;
        try {
            jsonTree = readTreeFromStream(mapper(), stream);
            portName = (String)jsonTree.get("portName").asText();
            portNumber = (String)jsonTree.get("portNumber").asText();
            portSpeed = (String)jsonTree.get("portSpeed").asText();
            name = (String)jsonTree.get("name").asText();
            minRate = (String)jsonTree.get("minRate").asText();
            maxRate = (String)jsonTree.get("maxRate").asText();
            burst = (String)jsonTree.get("burst").asText();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("IOException");
        }
        
        log.info(portName);
        log.info(portNumber);
        log.info(portSpeed);
        log.info(name);
        log.info(minRate);
        log.info(maxRate);
        log.info(burst);

        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(deviceId));

        if (device == null) {
            log.info("{} isn't support config.", deviceId);
            return Response.ok(root).build();
        }
        else {
            try{
                log.info("{} supports config.", deviceId);
                QueueDescription queueDesc = DefaultQueueDescription.builder()
                        .queueId(QueueId.queueId(name))
                        .maxRate(Bandwidth.kbps(Long.parseLong(maxRate)))
                        .minRate(Bandwidth.kbps(Long.valueOf(minRate)))
                        .burst(Long.valueOf(burst))
                        .build();
                log.info("queueDesc ok");

                //PortDescription portDesc = new DefaultPortDescription(PortNumber.portNumber(Long.valueOf(portNumber), name), true);

                PortDescription portDesc = DefaultPortDescription.builder()
                        .withPortNumber(PortNumber.portNumber(Long.valueOf(portNumber), portName))
                        .portSpeed(Long.valueOf(portSpeed))
                        .isEnabled(true)
                        .isRemoved(false)
                        .type(Port.Type.COPPER)
                        .build();

                log.info("portDesc ok");
                Map<Long, QueueDescription> queues = new HashMap<>();
                queues.put(0L, queueDesc);

                QosDescription qosDesc = DefaultQosDescription.builder()
                        .qosId(QosId.qosId(name))
                        .type(QosDescription.Type.HTB)
                        .maxRate(Bandwidth.kbps(Long.valueOf(maxRate)))
                        .queues(queues)
                        .build();

                log.info("qosDesc ok");
                QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
                QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
                PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);

                log.info("configs ok");
                
                queueConfig.deleteQueue(queueDesc.queueId());
                qosConfig.deleteQoS(qosDesc.qosId());
                portConfig.removeQoS(portDesc.portNumber());
                
            }
            catch(Exception e){
                e.printStackTrace();
                log.info(e.getMessage());
                log.info("ERROR");
                return ok(root).build();
            }

        }


        return ok(root).build();
    
    }

}
