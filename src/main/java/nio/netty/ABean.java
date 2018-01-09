package nio.netty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.msgpack.annotation.Message;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Message
public class ABean {

    private int id;
    private String name;


}
