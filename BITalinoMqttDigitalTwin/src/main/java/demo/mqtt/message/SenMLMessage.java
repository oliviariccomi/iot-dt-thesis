package demo.mqtt.message;

import demo.mqtt.utils.SenMLPack;
import demo.mqtt.utils.SenMLRecord;

/**
 * In the Digital Twin Project the SenML pack
 * send only the data related to the values I need:
 * - value
 * - timestamp
 */
public class SenMLMessage {

    private SensorMessage sensorMessage;

    private SenMLPack senMLPack;

    public SenMLMessage() {
    }

    public SenMLMessage(SensorMessage sensorMessage) {
        this.sensorMessage = sensorMessage;
        senMLPack = new SenMLPack();
        if (sensorMessage.getType().equals("EDA")) {
            EdaMessage edaMessage= (EdaMessage) sensorMessage;
            try {

                String macAddress = edaMessage.getMacAddress();
                String type = edaMessage.getType();
                int seqId = edaMessage.getSeqId();
                int dig1 = edaMessage.getDig1();
                int dig2 = edaMessage.getDig2();
                int dig3 = edaMessage.getDig3();
                int dig4 = edaMessage.getDig4();
                int frequency = edaMessage.getFrequency();
                Double value = edaMessage.getValue();
                Long timeS = edaMessage.getTimeS();

                SenMLRecord senMLRecord = new SenMLRecord();
                senMLRecord.setBn(macAddress);
                senMLRecord.setN(type);
                senMLRecord.setV(seqId);

                SenMLRecord senMLRecord1 = new SenMLRecord();
                senMLRecord1.setN(type);
                senMLRecord1.setV(dig1);
                senMLRecord1.setU("Binary");

                SenMLRecord senMLRecord2 = new SenMLRecord();
                senMLRecord2.setN(type);
                senMLRecord2.setV(dig2);
                senMLRecord2.setU("Binary");

                SenMLRecord senMLRecord3 = new SenMLRecord();
                senMLRecord3.setN(type);
                senMLRecord3.setV(dig3);
                senMLRecord3.setU("Binary");

                SenMLRecord senMLRecord4 = new SenMLRecord();
                senMLRecord4.setN(type);
                senMLRecord4.setV(dig4);
                senMLRecord4.setU("Binary");

                SenMLRecord senMLRecord5 = new SenMLRecord();
                senMLRecord5.setN(type);
                senMLRecord5.setV(frequency);
                senMLRecord5.setU("Hz");

                SenMLRecord senMLRecord6 = new SenMLRecord();
                senMLRecord6.setN(type);
                senMLRecord6.setV(value);
                senMLRecord6.setU("W");

                senMLPack.add(senMLRecord);
                senMLPack.add(senMLRecord1);
                senMLPack.add(senMLRecord2);
                senMLPack.add(senMLRecord3);
                senMLPack.add(senMLRecord4);
                senMLPack.add(senMLRecord5);
                senMLPack.add(senMLRecord6);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sensorMessage.getType().equals("ECG")) {
            EcgMessage ecgMessage = (EcgMessage) sensorMessage;
            try {
                String macAddress = ecgMessage.getMacAddress();
                String type = ecgMessage.getType();
                int seqId = ecgMessage.getSeqId();
                int dig1 = ecgMessage.getDig1();
                int dig2 = ecgMessage.getDig2();
                int dig3 = ecgMessage.getDig3();
                int dig4 = ecgMessage.getDig4();
                int frequency = ecgMessage.getFrequency();
                Double value = ecgMessage.getValue();
                Long timeS = ecgMessage.getTimeS();

                SenMLRecord senMLRecord = new SenMLRecord();
                senMLRecord.setBn(macAddress);
                senMLRecord.setN(type);
                senMLRecord.setBt(timeS);
                senMLRecord.setV(seqId);

                SenMLRecord senMLRecord1 = new SenMLRecord();
                senMLRecord1.setN(type);
                senMLRecord1.setV(dig1);
                senMLRecord1.setU("Binary");

                SenMLRecord senMLRecord2 = new SenMLRecord();
                senMLRecord2.setN(type);
                senMLRecord2.setV(dig2);
                senMLRecord2.setU("Binary");

                SenMLRecord senMLRecord3 = new SenMLRecord();
                senMLRecord3.setN(type);
                senMLRecord3.setV(dig3);
                senMLRecord3.setU("Binary");

                SenMLRecord senMLRecord4 = new SenMLRecord();
                senMLRecord4.setN(type);
                senMLRecord4.setV(dig4);
                senMLRecord4.setU("Binary");

                SenMLRecord senMLRecord5 = new SenMLRecord();
                senMLRecord5.setN(type);
                senMLRecord5.setV(frequency);
                senMLRecord5.setU("Hz");

                SenMLRecord senMLRecord6 = new SenMLRecord();
                senMLRecord6.setN(type);
                senMLRecord6.setV(value);
                senMLRecord6.setU("V");

                SenMLRecord senMLRecord7 = new SenMLRecord();
                senMLRecord7.setN(type);
                senMLRecord7.setT(timeS);

                senMLPack.add(senMLRecord);
                senMLPack.add(senMLRecord1);
                senMLPack.add(senMLRecord2);
                senMLPack.add(senMLRecord3);
                senMLPack.add(senMLRecord4);
                senMLPack.add(senMLRecord5);
                senMLPack.add(senMLRecord6);
                senMLPack.add(senMLRecord7);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public SenMLPack getSenMLPack() {
        return senMLPack;
    }

    public void setSenMLPack(SenMLPack senMLPack) {
        this.senMLPack = senMLPack;
    }

    @Override
    public String toString() {
        return "SenMLMessage{"+ senMLPack +
                '}';
    }
}
