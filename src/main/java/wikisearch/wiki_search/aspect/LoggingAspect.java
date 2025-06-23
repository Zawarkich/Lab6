package wikisearch.wiki_search.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* wikisearch.wiki_search.controller..*(..)) || execution(* wikisearch.wiki_search.service..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Вызван метод: {} с аргументами: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterThrowing(pointcut = "execution(* wikisearch.wiki_search..*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("Ошибка в методе: {} с аргументами: {}. Ошибка: {}", joinPoint.getSignature(), joinPoint.getArgs(), ex.getMessage(), ex);
    }
}
