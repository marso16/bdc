package com.capitalbanking.stage.config;

import lombok.Getter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

@Getter
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    private final byte[] cachedBody;

    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (InputStream is = request.getInputStream()) {
            byte[] buf = new byte[4096];
            int read;
            while ((read = is.read(buf)) != -1) {
                bos.write(buf, 0, read);
            }
        }
        this.cachedBody = bos.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bis = new ByteArrayInputStream(this.cachedBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return bis.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() {
                return bis.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}
