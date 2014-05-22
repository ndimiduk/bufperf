package bufperf;

import sun.nio.ch.DirectBuffer;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class DirectByteBufferUtils {
  static final Field BUFFER_ADDRESS;
  static final Field BUFFER_CAPACITY;

  static {
    try {
      BUFFER_ADDRESS = Buffer.class.getDeclaredField("address");
      BUFFER_ADDRESS.setAccessible(true);
      BUFFER_CAPACITY = Buffer.class.getDeclaredField("capacity");
      BUFFER_CAPACITY.setAccessible(true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Use the public API to retrieve a new slice on {@code source} of given length.
   */
  public static ByteBuffer getNextSlice(final ByteBuffer source, final int size) {
    ByteBuffer b = (ByteBuffer) source.slice().limit(size);
    source.position(source.position() + size);
    return b;
  }

  /**
   * Create an empty shell of a {@code DirectByteBuffer} instance that thinks
   * it is of the specified capacity.
   */
  public static ByteBuffer allocateDirectShell(final int size) throws Exception {
    ByteBuffer b = ByteBuffer.allocateDirect(0);
    BUFFER_CAPACITY.setInt(b, size);
    return b;
  }

  /**
   * Use reflection to update the state of {@code updateMe} to be as a slice of
   * {@code source} of the specified length.
   */
  public static void updateSlice(final DirectBuffer source, final DirectBuffer updateMe, final int size) throws Exception {
    ByteBuffer sourceAsBB = (ByteBuffer) source;
    ByteBuffer meAsBB = (ByteBuffer) updateMe;
    meAsBB.limit(size);
    BUFFER_ADDRESS.setLong(updateMe, source.address() + (sourceAsBB.position() << 0));
    BUFFER_CAPACITY.setInt(updateMe, size);
    meAsBB.clear();
    sourceAsBB.position(sourceAsBB.position() + size);
  }
}
