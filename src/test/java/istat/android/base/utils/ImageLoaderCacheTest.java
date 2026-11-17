package istat.android.base.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.io.InputStream;

import istat.android.base.memories.FileCache;

public class ImageLoaderCacheTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void failedCopyLeavesNoPartialCacheFile() throws IOException {
        FileCache cache = new FileCache(null, temporaryFolder.getRoot(), "test");
        final boolean[] disconnected = {false};

        try {
            ImageLoader.getBitmap("image", cache, null, ImageLoader.QUALITY_HIGH, new ImageLoader.ResourceConnectionHandler() {
                @Override
                public InputStream onConnect(String url) {
                    return new InputStream() {
                        private boolean firstRead = true;

                        @Override
                        public int read() throws IOException {
                            if (firstRead) {
                                firstRead = false;
                                return 42;
                            }
                            throw new IOException("Interrupted image stream");
                        }
                    };
                }

                @Override
                public void onDisconnect(String url, InputStream inputStream) {
                    disconnected[0] = true;
                }
            });
            fail("The interrupted stream must fail");
        } catch (IOException expected) {
            assertEquals("Interrupted image stream", expected.getMessage());
        }

        assertFalse(cache.resolve("image").exists());
        assertEquals(0, cache.getRootDir().list().length);
        assertTrue(disconnected[0]);
    }
}
