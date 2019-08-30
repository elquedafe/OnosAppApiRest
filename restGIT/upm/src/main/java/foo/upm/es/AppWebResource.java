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
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static org.onlab.util.Tools.readTreeFromStream;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Sample web resource.
 */
@Path("queues")
public class AppWebResource extends AbstractWebResource {
    private final Logger log = getLogger(getClass());
    private final ObjectNode root = mapper().createObjectNode();
    private final ArrayNode queueNode = root.putArray("queues");
    
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
        String portName = "";
        String portNumber = "";
        String portSpeed = "";
        long queueId = -1L;
        String minRate = "";
        String maxRate = "";
        String burst = "";
        long qosId = -1L;

        ObjectNode jsonTree = null;
        Map<Long,QueueDescription> queuesMap = null;

        try {
            jsonTree = readTreeFromStream(mapper(), stream);
            portName = (String)jsonTree.get("portName").asText();
            portNumber = (String)jsonTree.get("portNumber").asText();
            portSpeed = (String)jsonTree.get("portSpeed").asText();
            queueId = jsonTree.get("queueId").asLong();
            minRate = (String)jsonTree.get("minRate").asText();
            maxRate = (String)jsonTree.get("maxRate").asText();
            burst = (String)jsonTree.get("burst").asText();
            qosId = jsonTree.get("qosId").asLong();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("IOException");
        }

        log.info(portName);
        log.info(portNumber);
        log.info(portSpeed);
        log.info(String.valueOf(queueId));
        log.info(minRate);
        log.info(maxRate);
        log.info(burst);
        log.info(String.valueOf(qosId));

        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(deviceId));

        QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
        QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
        PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);

        QueueDescription queueDesc = DefaultQueueDescription.builder()
                .queueId(QueueId.queueId(String.valueOf(queueId)))
                .maxRate(Bandwidth.kbps(Long.parseLong(maxRate)))
                .minRate(Bandwidth.kbps(Long.parseLong(minRate)))
                .burst(Long.parseLong(burst+"000"))
                .build();

        PortDescription portDesc = DefaultPortDescription.builder()
                .withPortNumber(PortNumber.portNumber(Long.valueOf(portNumber), portName))
                .portSpeed(Long.parseLong(portSpeed+"000"))
                .isEnabled(true)
                .isRemoved(false)
                .type(Port.Type.COPPER)
                .build();

        QosDescription qosDescription = null;
        QueueDescription queueDescription = null;
        boolean existsQueue = false;
        boolean existsQos = false;

        //Get whether qosId exists or not
        for(QosDescription q : qosConfig.getQoses()) {
            if (q.qosId().name().equals(String.valueOf(qosId))) {
                existsQos = true;
                qosDescription = q;
                break;
            }
        }
        //Get whether queueId exists or not
        for(QueueDescription q : queueConfig.getQueues()){
            if(q.queueId().name().equals(String.valueOf(queueId))){
                existsQueue = true;
                break;
            }
        }

        if(existsQueue){
            return Response.status(Response.Status.CONFLICT).entity("Queue already exists").build();
        }
        else if(existsQos){
            //if queue list of qos is empty add, else create new queue list
            if(!qosDescription.queues().get().isEmpty()){
                log.info("Queues are present in QoS");
                queuesMap = qosDescription.queues().get();
            }
            else{
                log.info("Queues not present in QoS");
                queuesMap = new HashMap<>();
            }
            queuesMap.put(Long.valueOf(queueDesc.queueId().toString()), queueDesc);


            queueConfig.addQueue(queueDesc);
            log.info("Queue added");
            qosConfig.insertQueues(qosDescription.qosId(), queuesMap);
            log.info("Queue added to existing QoS");

        }
        //IF there is no that queue and that qos
        else{
            log.info("Selected qos and queue no exist");

            //add queue to list
            queuesMap = new HashMap<>();
            queuesMap.put(Long.valueOf(queueDesc.queueId().name()), queueDesc);
            log.info("Queue added to map: {}", queueDesc.toString());

            //create new qos
            QosDescription qosDesc = DefaultQosDescription.builder()
                    .qosId(QosId.qosId(String.valueOf(qosId)))
                    .type(QosDescription.Type.HTB)
                    .maxRate(Bandwidth.kbps(Long.valueOf(maxRate)))
                    .queues(queuesMap)
                    .build();

            //add queue
            queueConfig.addQueue(queueDesc);
            log.info("Queue added");
            //add qos
            qosConfig.addQoS(qosDesc);
            log.info("QoS added: {}", qosDesc.toString());
            //add qos to port
            portConfig.applyQoS(portDesc, qosDesc);
            log.info("Port added");
        }
        return Response.noContent().build();
    
    }

    /**
     * Get hello world greeting.
     *
     * @return 200 OK
     */
    @POST
    @Path("test/{deviceId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addQueueTest(@PathParam("deviceId") String deviceId,
                            InputStream stream)  {
        String portName = "s2-eth1";
        String portNumber = "1";
        String portSpeed = "10000";
        String minRate = "20000";
        String maxRate = "20000";
        String burst = "20000";
        String qosId = "0";
        
        log.info(portName);
        log.info(portNumber);
        log.info(portSpeed);
        log.info(minRate);
        log.info(maxRate);
        log.info(burst);
        log.info(qosId);

        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(deviceId));

        if (device == null) {
            log.info("{} isn't support config.", deviceId);
            return Response.ok(root).build();
        }
        else {
            
                log.info("{} supports config.", deviceId);

                QueueDescription queueDesc = DefaultQueueDescription.builder()
                        .queueId(QueueId.queueId("0"))
                        .maxRate(Bandwidth.kbps(Long.parseLong("10000000")))
                        .minRate(Bandwidth.kbps(Long.parseLong("0")))
                        .burst(Long.parseLong("10000000000"))
                        .build();
                QueueDescription queueDesc2 = DefaultQueueDescription.builder()
                        .queueId(QueueId.queueId("1"))
                        .maxRate(Bandwidth.kbps(Long.parseLong(maxRate)))
                        .minRate(Bandwidth.kbps(Long.parseLong(minRate)))
                        .burst(Long.parseLong(maxRate+"000"))
                        .build();

                QueueDescription queueDesc3 = DefaultQueueDescription.builder()
                        .queueId(QueueId.queueId("1"))
                        .maxRate(Bandwidth.kbps(Long.parseLong("20000")))
                        .minRate(Bandwidth.kbps(Long.parseLong("20000")))
                        .burst(Long.parseLong("20000000"))
                        .build();
                
                log.info("QueueDescriptions ok");

                //PortDescription portDesc = new DefaultPortDescription(PortNumber.portNumber(Long.valueOf(portNumber), name), true);

                PortDescription portDesc = DefaultPortDescription.builder()
                        .withPortNumber(PortNumber.portNumber(Long.valueOf(portNumber), portName))
                        .portSpeed(Long.parseLong(portSpeed+"000"))
                        .isEnabled(true)
                        .isRemoved(false)
                        .type(Port.Type.COPPER)
                        .build();

                PortDescription portDesc2 = DefaultPortDescription.builder()
                        .withPortNumber(PortNumber.portNumber(Long.valueOf("1"), "s3-eth1"))
                        .portSpeed(Long.parseLong(portSpeed+"000"))
                        .isEnabled(true)
                        .isRemoved(false)
                        .type(Port.Type.COPPER)
                        .build();

                log.info("PortDescription ok");
                Map<Long, QueueDescription> queues = new HashMap<>();
                queues.put(Long.valueOf(queueDesc.queueId().name()), queueDesc);
                queues.put(Long.valueOf(queueDesc2.queueId().name()), queueDesc2);

                Map<Long, QueueDescription> queues2 = new HashMap<>();
                queues2.put(Long.valueOf(queueDesc.queueId().name()), queueDesc);
                queues2.put(Long.valueOf(queueDesc3.queueId().name()), queueDesc);

                QosDescription qosDesc = DefaultQosDescription.builder()
                        .qosId(QosId.qosId(qosId))
                        .type(QosDescription.Type.HTB)
                        .maxRate(Bandwidth.kbps(Long.valueOf("10000000")))
                        .queues(queues)
                        .build();

                QosDescription qosDesc2 = DefaultQosDescription.builder()
                        .qosId(QosId.qosId(qosId))
                        .type(QosDescription.Type.HTB)
                        .maxRate(Bandwidth.kbps(Long.valueOf("10000000")))
                        .queues(queues2)
                        .build();

                log.info("QosDescription ok");

                QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
                QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
                PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);

                log.info("Behaviour loaded ok");

                queueConfig.addQueue(queueDesc);
                queueConfig.addQueue(queueDesc2);
                queueConfig.addQueue(queueDesc3);
                log.info("ADDED QUEUES:\n {} \n {}", queueDesc.toString(), queueDesc2.toString());
                qosConfig.addQoS(qosDesc);
                qosConfig.addQoS(qosDesc2);
                log.info("QoS added");
                portConfig.applyQoS(portDesc, qosDesc);
                portConfig.applyQoS(portDesc2, qosDesc2);
                log.info("Port added");
                
            }


        return Response.noContent().build();
    
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

        QueueObject queue = null;
        log.info(deviceId);
        
        log.info(deviceId);
        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(deviceId));

        if(device ==null)
            log.info("device null");
        else {
            log.info("device not null");
        
            QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
            log.info("queue config ok");
            
            QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
            log.info("classes qos loaded");

            for(QueueDescription q : queueConfig.getQueues()){
                queue = new QueueObject(q.queueId().name(), q.minRate().get().bps(), q.maxRate().get().bps(), q.burst().get());
                
                queueNode.add(mapper().valueToTree(queue));

                log.info("name={}, type={}, dscp={}, maxRate={}, " +
                                "minRate={}, pri={}, burst={}", q.queueId(), q.type(),
                        q.dscp(), q.maxRate(), q.minRate(),
                        q.priority(), q.burst());
            }

            /*queueConfig.getQueues().stream().forEach(q -> {
                queue = new QueueObject(q.queueId().name(), q.minRate().get().bps(), q.maxRate().get().bps(), q.burst().get());
                node = mapper().valueToTree(queue);
                queueNode.add(node);

                log.info("name={}, type={}, dscp={}, maxRate={}, " +
                                "minRate={}, pri={}, burst={}", q.queueId(), q.type(),
                        q.dscp(), q.maxRate(), q.minRate(),
                        q.priority(), q.burst());
            });*/

            qosConfig.getQoses().forEach(q -> {
                
                log.info("name={}, maxRate={}, cbs={}, cir={}, " +
                                "queues={}, type={}", q.qosId(), q.maxRate(),
                        q.cbs(), q.cir(), q.queues(), q.type());

            });
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
    @Path("{deviceId}/{queueId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQueues(@PathParam("deviceId") String deviceId, @PathParam("queueId") String queueId) {

        QueueObject queue = null;
        log.info(deviceId);
        
        log.info(deviceId);
        DeviceService deviceService = DefaultServiceDirectory.getService(DeviceService.class);
        Device device = deviceService.getDevice(DeviceId.deviceId(deviceId));

        if(device ==null)
            log.info("device null");
        else {
            log.info("device not null");
        
            QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
            log.info("queue config ok");
            
            QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
            log.info("classes qos loaded");

            for(QueueDescription q : queueConfig.getQueues()){
                if(q.queueId().name().equals(queueId)){
                    queue = new QueueObject(q.queueId().name(), q.minRate().get().bps(), q.maxRate().get().bps(), q.burst().get());
                    
                    queueNode.add(mapper().valueToTree(queue));

                    log.info("name={}, type={}, dscp={}, maxRate={}, " +
                                    "minRate={}, pri={}, burst={}", q.queueId(), q.type(),
                            q.dscp(), q.maxRate(), q.minRate(),
                            q.priority(), q.burst());
                    break;
                }
            }

            /*queueConfig.getQueues().stream().forEach(q -> {
                queue = new QueueObject(q.queueId().name(), q.minRate().get().bps(), q.maxRate().get().bps(), q.burst().get());
                node = mapper().valueToTree(queue);
                queueNode.add(node);

                log.info("name={}, type={}, dscp={}, maxRate={}, " +
                                "minRate={}, pri={}, burst={}", q.queueId(), q.type(),
                        q.dscp(), q.maxRate(), q.minRate(),
                        q.priority(), q.burst());
            });*/

            qosConfig.getQoses().forEach(q -> {
                
                log.info("name={}, maxRate={}, cbs={}, cir={}, " +
                                "queues={}, type={}", q.qosId(), q.maxRate(),
                        q.cbs(), q.cir(), q.queues(), q.type());

            });
        }

        return ok(root).build();
    }

    /**
     * Get hello world greeting.
     *
     * @return 200 OK
     */
    @DELETE
    @Path("delete/{deviceId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteQueue(@PathParam("deviceId") String deviceId, 
                            @QueryParam("portName    ") String portName,
                            @QueryParam("portNumber") String portNumber,
                            @QueryParam("portSpeed") String portSpeed,
                            @QueryParam("queueId") String queueId,
                            @QueryParam("minRate") String minRate,
                            @QueryParam("maxRate") String maxRate,
                            @QueryParam("burst") String burst,
                            @QueryParam("qosId") String qosId,
                            InputStream stream)  {
        

        /*String portName = "";
        String portNumber = "";
        String portSpeed = "";
        String name = "";
        String minRate = "";
        String maxRate = "";
        String burst = "";
        String qosId = "";

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
            qosId = (String)jsonTree.get("qosId").asText();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("IOException");
        }*/
        
        log.info(portName);
        log.info(portNumber);
        log.info(portSpeed);
        log.info(queueId);
        log.info(minRate);
        log.info(maxRate);
        log.info(burst);
        log.info(qosId);

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
                        .queueId(QueueId.queueId(queueId))
                        .maxRate(Bandwidth.kbps(Long.parseLong(maxRate)))
                        .minRate(Bandwidth.kbps(Long.valueOf(minRate)))
                        .burst(Long.valueOf(burst+"000"))
                        .build();
                log.info("queueDesc ok");

                //PortDescription portDesc = new DefaultPortDescription(PortNumber.portNumber(Long.valueOf(portNumber), name), true);

                PortDescription portDesc = DefaultPortDescription.builder()
                        .withPortNumber(PortNumber.portNumber(Long.valueOf(portNumber), portName))
                        .portSpeed(Long.valueOf(portSpeed+"000"))
                        .isEnabled(true)
                        .isRemoved(false)
                        .type(Port.Type.COPPER)
                        .build();

                log.info("portDesc ok");
                Map<Long, QueueDescription> queues = new HashMap<>();
                queues.put(0L, queueDesc);

                QosDescription qosDesc = DefaultQosDescription.builder()
                        .qosId(QosId.qosId(qosId))
                        .type(QosDescription.Type.HTB)
                        .maxRate(Bandwidth.kbps(Long.valueOf(maxRate)))
                        .queues(queues)
                        .build();

                log.info("qosDesc ok");
                QueueConfigBehaviour queueConfig = device.as(QueueConfigBehaviour.class);
                QosConfigBehaviour qosConfig = device.as(QosConfigBehaviour.class);
                PortConfigBehaviour portConfig = device.as(PortConfigBehaviour.class);

                log.info("configs ok");
                
                //DELETE

                //1
                /*queueConfig.deleteQueue(queueDesc.queueId());
                log.info("Deleted queue");
                qosConfig.deleteQoS(qosDesc.qosId());
                log.info("Deleted QoS");
                portConfig.removeQoS(portDesc.portNumber());
                log.info("Deleted Port QoS");*/

                //2 
                if(qosConfig.getQoses().size() > 0){
                    log.info("QoS > 0");
                    Object o = qosConfig.getQoses().toArray()[0];
                    QosDescription qosDescription = null;
                    if(o instanceof QosDescription){
                        log.info("Is QosDescription");
                        qosDescription = (QosDescription)o;

                        
                        //If QoS does not have queues delete QoS
                        if(!qosDescription.queues().isPresent()){
                            log.info("No queues in QoS");
                            queueConfig.deleteQueue(queueDesc.queueId());
                            qosConfig.deleteQoS(qosDesc.qosId());
                            portConfig.removeQoS(portDesc.portNumber());
                        }
                        else{
                            log.info("There are queues in QoS");
                            Map<Long,QueueDescription> queuesOld = qosDescription.queues().get();
                            queuesOld.remove(Long.valueOf(queueDesc.queueId().name()));
                            queueConfig.deleteQueue(queueDesc.queueId());
                        }
                    
                        //2
                        /*List<QosDescription> qoses = (ArrayList<QosDescription>)qosConfig.getQoses();
                        QosDescription qosDescription = qoses.get(0);*/

                        
                        
                    }
                }
                else
                    return Response.status(Response.Status.CONFLICT).build();
                
            }
            catch(Exception e){
                e.printStackTrace();
                log.info(e.getMessage());
                log.info("ERROR");
                return ok(e.getMessage()).build();
            }

        }


        return Response.noContent().build();
    
    }

    private QosDescription getQoSFromQueue(QosConfigBehaviour qosConfig, QueueDescription queueDes){
        QosDescription qosDescription = null;

        qosConfig.getQoses().forEach(q -> {
            
            log.info("name={}, maxRate={}, cbs={}, cir={}, " +
                            "queues={}, type={}", q.qosId(), q.maxRate(),
                    q.cbs(), q.cir(), q.queues(), q.type());

        });
        return qosDescription;
    }

}