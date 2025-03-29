package mate.academy.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isHealthy = checkApplicationHealth();

        if (isHealthy) {
            return Health.up().withDetail("status", "Application is healthy").build();
        } else {
            return Health.down().withDetail("status", "Application is unhealthy").build();
        }
    }

    private boolean checkApplicationHealth() {
        return true;
    }
}
