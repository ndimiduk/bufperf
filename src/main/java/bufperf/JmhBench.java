package bufperf;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;

public class JmhBench {

  static byte VAL = (byte) 128;
  static int SIZE = 1024;

  @State(Scope.Thread)
  public static class ThreadState {
    ByteBuffer mySource;

    @Setup
    public void setup() throws Exception {
      mySource = ByteBuffer.allocateDirect(SIZE);
      mySource.put(VAL);
      mySource.clear();
    }
  }

  @State(Scope.Thread)
  public static class ReflectionState extends ThreadState {
    ByteBuffer s;
    DirectBuffer sAsDirect;
    DirectBuffer sourceAsDirect;

    @Setup()
    public void setup() throws Exception {
      super.setup();
      sourceAsDirect = (DirectBuffer) mySource;
      s = DirectByteBufferUtils.allocateDirectShell(mySource.capacity());
      sAsDirect = (DirectBuffer) s;
    }
  }

  @GenerateMicroBenchmark
  public ByteBuffer usePublicApi(ThreadState ts) {
    ByteBuffer b = DirectByteBufferUtils.getNextSlice(ts.mySource, SIZE);
    ts.mySource.clear();
    return b;
  }

  @GenerateMicroBenchmark
  public ByteBuffer useReflection(ReflectionState rs) throws Exception {
    DirectByteBufferUtils.updateSlice(rs.sourceAsDirect, rs.sAsDirect, SIZE);
    rs.mySource.clear();
    return rs.s;
  }
}
