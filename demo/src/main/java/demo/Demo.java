package demo;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError
 * @author kate
 * @create 2019/7/29
 * @since 1.0.0
 */
public class Demo {
   class OOMObject {

  }

  /**
   * -Xms20M -Xmx20M -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError
   */
  @Test
  public  void test() {
    List<OOMObject> list = new ArrayList<OOMObject>();
    while (true) {
      list.add(new OOMObject());

    }
  }
}