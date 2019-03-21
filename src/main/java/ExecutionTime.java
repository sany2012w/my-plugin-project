import com.intellij.execution.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;

import static com.intellij.execution.process.ProcessOutputTypes.STDOUT;
import static com.intellij.openapi.util.Clock.getTime;

public class ExecutionTime implements ProjectComponent {

    private long startTime = 0;

    public ExecutionTime(@NotNull Project project){
        MessageBusConnection busConnection = project.getMessageBus().connect();
        busConnection.subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
                startTime = getTime();
            }

            @Override
            public void processTerminated(
                    @NotNull String executorId, @NotNull ExecutionEnvironment env,
                    @NotNull ProcessHandler handler, int exitCode) {
                float exTime = getTime() - startTime;
                String msg = " minutes";
                if (exTime < 60000) {
                    if (exTime < 1000) {
                        msg = " milliseconds";
                    }
                    else {
                        msg = " seconds";
                        exTime /= 1000;
                    }
                }
                else {
                    exTime /= 60000;
                }
                handler.notifyTextAvailable("Code executed by: " + (exTime) + msg, STDOUT );

            }
        });

    }


}
