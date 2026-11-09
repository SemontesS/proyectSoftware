package application.adapters.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Instant;

/** Writes the error body for failures raised by the security filter chain, before any controller runs. */
final class JsonErrorWriter {

    private JsonErrorWriter() {
    }

    static void write(HttpServletRequest request, HttpServletResponse response,
                      int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write("{\"timestamp\":\"" + Instant.now() + "\",\"status\":" + status
                + ",\"error\":\"" + error + "\",\"message\":\"" + escape(message)
                + "\",\"path\":\"" + escape(request.getRequestURI()) + "\"}");
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
