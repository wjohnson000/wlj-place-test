package std.wlj.cassandra;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

@UDT(name="InterpResult", caseSensitiveKeyspace=true, caseSensitiveType=true)
public class InterpResult {

    @Field(name="rep_id")
    private int repId;

    @Field(name="raw_score")
    private short rawScore;

    @Field(name="rel_score")
    private short relScore;

    public InterpResult() { }

    public InterpResult(int repId, int rawScore, int relScore) {
        this.repId = repId;
        this.rawScore = (short)rawScore;
        this.relScore = (short)relScore;
    }

    public int getRepId() {
        return repId;
    }

    public void setRepId(int repId) {
        this.repId = repId;
    }

    public short getRawScore() {
        return rawScore;
    }

    public void setRawScore(short rawScore) {
        this.rawScore = rawScore;
    }

    public short getRelScore() {
        return relScore;
    }

    public void setRelScore(short relScore) {
        this.relScore = relScore;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("rep_id: ").append(repId);
        buff.append("; raw_score: ").append(rawScore);
        buff.append("; rel_score: ").append(relScore);
        return buff.toString();
    }
}
