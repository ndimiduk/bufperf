package bufperf;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;

public class NaiveBench {

  static void logfmt(String fmt, Object... args) {
    System.out.println(String.format(fmt, args));
  }

  public static void main(String[] argv) throws Exception {
    long start, end;

    start = System.nanoTime();
    ByteBuffer source = ByteBuffer.allocateDirect(1024 * 1024 * 1024);
    end = System.nanoTime();
//    logfmt("Allocated %s direct bytes in %s ms.", 1024 * 1024 * 1024, (end - start / 1e6));

    start = System.nanoTime();
    byte val = Byte.MIN_VALUE;
    for (int i = 0; i < 1024 * 1024; i++) {
      source.put(val++);
      source.position(source.position() + 1023);
    }
    end = System.nanoTime();
//    logfmt("Initialized source in %s ms.", (end - start) / 1e6);
    source.clear();

    val = Byte.MIN_VALUE;
    start = System.nanoTime();
    for (int i = 0; i < 1024 * 1024; i++) {
      ByteBuffer s = DirectByteBufferUtils.getNextSlice(source, 1024);
      assert s.get() == val++;
//      logfmt("0x%s: %s", Long.toHexString(((DirectBuffer)s).address()), Long.toHexString(s.getLong()));
    }
    end = System.nanoTime();
    logfmt("getNextSlice verified %s slices in %s ms.", 1024 * 1024, (end - start) / 1e6);
    logfmt("getNextSlice: %s ops/ms", (1024 * 1024) / ((end - start) / 1e6));
    source.flip();

    val = Byte.MIN_VALUE;
    start = System.nanoTime();
    ByteBuffer s = DirectByteBufferUtils.allocateDirectShell(1024);
    DirectBuffer sAsDirect = (DirectBuffer) s;
    DirectBuffer sourceAsDirect = (DirectBuffer) source;
    for (int i = 0; i < 1024 * 1024; i++) {
      DirectByteBufferUtils.updateSlice(sourceAsDirect, sAsDirect, 1024);
      assert s.get() == val++;
//      logfmt("0x%s: %s", Long.toHexString(((DirectBuffer)s).address()), Long.toHexString(s.getLong()));
    }
    end = System.nanoTime();
    logfmt("updateSlice verified %s slices in %s ms.", 1024 * 1024, (end - start) / 1e6);
    logfmt("updateSlice: %s ops/ms", (1024 * 1024) / ((end - start) / 1e6));
  }
}
