package com.example.todo.service;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UpdatePastDueStatusSchedulerTest {

  @SpyBean private UpdatePastDueStatusScheduler mockScheduler;

  @Test
  void scheduler_shouldRunAtleast2TimesIn15Seconds() {
    await()
        .atMost(new Duration(15, SECONDS))
        .untilAsserted(() -> verify(mockScheduler, atLeast(2)).updatePastDueStatus());
  }
}
