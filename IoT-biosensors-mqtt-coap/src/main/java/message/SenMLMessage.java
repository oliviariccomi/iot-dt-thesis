package message;

import utils.MySenMLRecord;
import utils.SenMLPack;

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

                MySenMLRecord mySenMLRecord = new MySenMLRecord();
                mySenMLRecord.setBn(macAddress);
                mySenMLRecord.setN(type);
                mySenMLRecord.setS(seqId);
                mySenMLRecord.setD1(dig1);
                mySenMLRecord.setD2(dig2);
                mySenMLRecord.setD3(dig3);
                mySenMLRecord.setD4(dig4);
                mySenMLRecord.setF(frequency);
                mySenMLRecord.setV(value);
                mySenMLRecord.setT(timeS);

                senMLPack.add(mySenMLRecord);

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

                MySenMLRecord mySenMLRecord = new MySenMLRecord();
                mySenMLRecord.setBn(macAddress);
                mySenMLRecord.setN(type);
                mySenMLRecord.setS(seqId);
                mySenMLRecord.setD1(dig1);
                mySenMLRecord.setD2(dig2);
                mySenMLRecord.setD3(dig3);
                mySenMLRecord.setD4(dig4);
                mySenMLRecord.setF(frequency);
                mySenMLRecord.setV(value);
                mySenMLRecord.setT(timeS);

                senMLPack.add(mySenMLRecord);

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
        return "SenMLMessage{" +
                "senMLPack=" + senMLPack +
                '}';
    }
}
