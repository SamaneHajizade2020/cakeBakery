package cakeBakery;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Result {

    private @Id
    @GeneratedValue
    Long id;
    private String count;

    public Result(Long id, String name) {
        this.id = id;
        this.count = name;
    }

    public Result(Long quantity) {
    }

    public Result() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
