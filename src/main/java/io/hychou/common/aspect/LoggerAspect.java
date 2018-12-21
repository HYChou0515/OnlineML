package io.hychou.common.aspect;

import io.hychou.common.exception.service.ServiceException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Collection;

import static org.slf4j.LoggerFactory.getLogger;


@Aspect
@Component
public class LoggerAspect {

    private static final String SERVICE_LAYER="SERVICE";
    private static final String CONTROLLER_LAYER="CONTROLLER";
    private static final String DAO_LAYER="DAO";

    @Around("execution(* io.hychou..service.impl.*ServiceImpl.*(..))")
    public Object serviceLogMethod(final ProceedingJoinPoint joinPoint)
            throws Throwable {
        return logMethod(joinPoint, SERVICE_LAYER);
    }

    @Around("execution(* io.hychou..controller.*Controller.*(..))")
    public Object controllerLogMethod(final ProceedingJoinPoint joinPoint)
            throws Throwable {
        return logMethod(joinPoint, CONTROLLER_LAYER);
    }

    @Around("execution(* io.hychou..dao.*Repository.*(..))")
        public Object DaoLogMethod(final ProceedingJoinPoint joinPoint)
            throws Throwable {
            return logMethod(joinPoint, DAO_LAYER);
    }


    private Object logMethod(final ProceedingJoinPoint joinPoint, String layer)
            throws Throwable {
        final Class<?> targetClass = joinPoint.getTarget().getClass();
        final Logger logger = getLogger(targetClass);
        try {
            final String className = targetClass.getSimpleName();
            logger.info(getPreMessage(joinPoint, className, layer));
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            final Object retVal = joinPoint.proceed();
            stopWatch.stop();
            logger.info(getPostMessage(joinPoint, className, layer, stopWatch.getTotalTimeMillis()));
            return retVal;
        } catch ( final ServiceException ex) {
            logger.warn(getErrorMessage(ex));
            throw ex;
        } catch ( final Throwable ex ) {
            logger.error(getErrorMessage(ex), ex);
            throw ex;
        }
    }

    private static String getPreMessage(final JoinPoint joinPoint, final String className, final String layer) {
        final StringBuilder builder = new StringBuilder()
                .append(layer)
                .append(": Entered in ").append(className).append(".")
                .append(joinPoint.getSignature().getName())
                .append("(");
        appendTo(builder, joinPoint);
        return builder
                .append(")")
                .toString();
    }

    private static String getPostMessage(final JoinPoint joinPoint, final String className, final String layer, final long millis) {
        return layer + ": Exit from " + className + "." +
                joinPoint.getSignature().getName() +
                "(..); Execution time: " +
                millis +
                " ms;";
    }

    private static String getErrorMessage(final Throwable ex) {
        return ex.getMessage();
    }

    private static void appendTo(final StringBuilder builder, final JoinPoint joinPoint) {
        final Object[] args = joinPoint.getArgs();
        for ( int i = 0; i < args.length; i++ ) {
            if ( i != 0 ) {
                builder.append(", ");
            }

            String argClassName;
            try {
                argClassName = args[i].getClass().getSimpleName()+" ";
            } catch (Exception e) {
                argClassName = "";
            }
            builder.append(argClassName)
                    .append(args[i]);
            getSizeIfIsArrayOrCollection(builder, args[i]);
        }
    }

    private static void getSizeIfIsArrayOrCollection(final StringBuilder builder, final Object arg) {
        Integer len = null;
        if (arg instanceof Collection<?>) len = ((Collection<?>) arg).size();
        if (arg instanceof byte[]) len = ((byte[]) arg).length;
        if (arg instanceof short[]) len = ((short[]) arg).length;
        if (arg instanceof int[]) len = ((int[]) arg).length;
        if (arg instanceof long[]) len = ((long[]) arg).length;
        if (arg instanceof float[]) len = ((float[]) arg).length;
        if (arg instanceof double[]) len = ((double[]) arg).length;
        if (arg instanceof char[]) len = ((char[]) arg).length;
        if (arg instanceof String[]) len = ((String[]) arg).length;
        if (arg instanceof boolean[]) len = ((boolean[]) arg).length;
        if (arg instanceof Object[]) len = ((Object[]) arg).length;

        if(len != null) {
            builder.append("(length=").append(len).append(")");
        }
    }

}