package works.hop.javro.jdbc.template;

import works.hop.javro.jdbc.annotation.Query;
import works.hop.javro.jdbc.template.toremove.InsertTemplate;
import works.hop.javro.jdbc.template.toremove.SelectTemplate;
import works.hop.javro.jdbc.template.toremove.UpdateTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CrudRepoHandler<T, I> implements InvocationHandler {

    final Class<? extends CrudRepo<T, I>> target;
    final List<Method> implementedMethods = Arrays.asList(CrudRepo.class.getMethods());
    final List<String> queryTypes = List.of("select", "find", "insert", "create", "update", "delete");
    final CrudRepo<T, I> repoInstance = new CrudRepo<>() {
        @Override
        public Class<? extends CrudRepo<T, I>> getInstanceType() {
            return target;
        }
    };

    public CrudRepoHandler(Class<? extends CrudRepo<T, I>> target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (implementedMethods.contains(method)) {
            return method.invoke(repoInstance, args);
        } else {
            Query queryAnnotation = method.getAnnotation(Query.class);
            if (queryAnnotation == null) {
                throw new RuntimeException("Query annotation is missing for method '" + method.getName() + "'");
            }
            Optional<String> queryType = queryTypes.stream().filter(type -> method.getName().startsWith(type)).findFirst();
            if (queryType.isEmpty()) {
                throw new RuntimeException("Expected method name to start with either of '" + queryType + "'");
            }
            String queryString = queryAnnotation.value();
            boolean isCollection = Collection.class.isAssignableFrom(method.getReturnType());
            switch (queryType.get()) {
                case "select":
                case "find":
                    ParameterizedType genericSuperclass = (ParameterizedType) target.getGenericInterfaces()[0];
                    Class<T> resultType = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
                    if (isCollection) {
                        return SelectTemplate.executeQueryList(queryString, args, resultType);
                    }
                    return SelectTemplate.executeQuery(queryString, args, resultType);
                case "insert":
                case "create":
                    return InsertTemplate.executeUpdate(queryString, args);
                case "update":
                    return UpdateTemplate.executeUpdate(queryString, args);
                case "delete":
                    return DeleteTemplate.executeUpdate(queryString, args);
                default:
                    throw new RuntimeException("Unknown case. Look into this further");
            }
        }
    }
}