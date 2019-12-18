package root.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SecData {
    @Column
    int test;

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }
}
