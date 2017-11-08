package concurrency;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class ThreadPoolExecutorInvokeAny {

    @AllArgsConstructor
    @Getter
    static class UserValidator {

        private String name;

        public boolean validate(String name, String password) {
            Random random = new Random();
            long duration = (long) (Math.random() * 10);
            System.out.printf("Validator %s: Validating a user durng %d seconds\n", this.name, duration);
            try {
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return random.nextBoolean();
        }
    }

    @AllArgsConstructor
    static class TaskValidator implements Callable<String> {

        private UserValidator userValidator;
        private String user;
        private String password;

        @Override
        public String call() throws Exception {
            if (!userValidator.validate(user, password)) {
                System.out.printf("%s: The user has not been found\n", userValidator.getName());
                throw new Exception("Error validating user");
            }
            System.out.printf("%s: The user has been found\n", userValidator.getName());
            return userValidator.getName();
        }

    }

    public static void main(String[] args) {
        String userName = "test";
        String password = "123456";
        UserValidator ldapValidator = new UserValidator("LDAP");
        UserValidator dbValidator = new UserValidator("Mysql");
        TaskValidator ldapTask = new TaskValidator(ldapValidator, userName, password);
        TaskValidator dbTask = new TaskValidator(dbValidator, userName, password);
        List<TaskValidator> taskList = Lists.newArrayList(ldapTask, dbTask);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            System.out.printf("Main: result: %s\n", executor.invokeAny(taskList));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        System.out.println("Main: End of execution");
    }
}
