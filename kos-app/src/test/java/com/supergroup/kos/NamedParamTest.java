package com.supergroup.kos;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supergroup.core.utils.NamedParameterInterpolation;
import com.supergroup.kos.notification.domain.repository.NotificationTemplateRepository;

@SpringBootTest
public class NamedParamTest {
    @Autowired
    private NotificationTemplateRepository templateRepo;

    @Test
    public void test() {
        try {
            String message = "${atk1}(+<color=#00ff00>${atk1_growth}%%</color>)";
            Map<String, Object> params= new HashMap<>();
            params.put("atk1", 1L);
            params.put("atk1_growth", 1L);
                NamedParameterInterpolation.format(message, params, true);



        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
