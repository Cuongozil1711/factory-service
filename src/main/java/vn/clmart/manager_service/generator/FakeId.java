package vn.clmart.manager_service.generator;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

public class FakeId {
    private static FakeId instance;
    private final long nodeId;
    private final long customEpoch;
    private volatile long lastTimestamp;
    private volatile long sequence;

    public static FakeId getInstance() {
        if (instance == null) {
            instance = new FakeId();
        }
        return instance;
    }

    public static FakeId init() {
        return new FakeId();
    }

    public FakeId() {
        this.lastTimestamp = -1L;
        this.sequence = 0L;
        this.nodeId = this.createNodeId();
        this.customEpoch = 1622505600000L;
    }

    public synchronized Long nextId() {
        long currentTimestamp = this.timestamp();
        if (currentTimestamp < this.lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        } else {
            if (currentTimestamp == this.lastTimestamp) {
                this.sequence = this.sequence + 1L & 1023L;
                if (this.sequence == 0L) {
                    currentTimestamp = this.waitNextMillis(currentTimestamp);
                }
            } else {
                this.sequence = 0L;
            }

            this.lastTimestamp = currentTimestamp;
            long id = currentTimestamp << 16 | this.nodeId << 10 | this.sequence;
            return id;
        }
    }

    private long timestamp() {
        return Instant.now().toEpochMilli() - this.customEpoch;
    }

    private long waitNextMillis(long currentTimestamp) {
        while(currentTimestamp == this.lastTimestamp) {
            currentTimestamp = this.timestamp();
        }

        return currentTimestamp;
    }

    private long createNodeId() {
        long nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();

            label29:
            while(true) {
                byte[] mac;
                do {
                    if (!networkInterfaces.hasMoreElements()) {
                        nodeId = (long)sb.toString().hashCode();
                        break label29;
                    }

                    NetworkInterface networkInterface = (NetworkInterface)networkInterfaces.nextElement();
                    mac = networkInterface.getHardwareAddress();
                } while(mac == null);

                byte[] var7 = mac;
                int var8 = mac.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    byte macPort = var7[var9];
                    sb.append(String.format("%02X", macPort));
                }
            }
        } catch (Exception var11) {
            nodeId = (long)(new SecureRandom()).nextInt();
        }

        nodeId &= 63L;
        return nodeId;
    }
}
