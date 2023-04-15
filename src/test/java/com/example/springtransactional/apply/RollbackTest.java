package com.example.springtransactional.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService service;

    @Test
    void runtimeException() {
        service.runtimeException();
    }

    @TestConfiguration
    static class RollbackTestConfig {

        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {

        // 런타임 예외 발생: 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // 체크 예외 발생: 커밋
        @Transactional
        public void checkedException() throws MyException {
            log.info("checkedException");

            // 왜 이렇게 클래스를 생성하여 이렇게 할까?
            // '체크 예외' 이기 때문에 잡거나(try), 던져야(메서드 옆 throws) 되기 때문이다.
            // Exception의 자식 예외가 터졌기 때문에 이건 체크 예외.
            throw new MyException();
        }


        // 체크 예외 rollbackFor 지정: 롤백
        // 즉, 이건 "체크 예외지만 롤백 할거야" 하는 것이다.
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

    }

    static class MyException extends Exception {

    }


}
