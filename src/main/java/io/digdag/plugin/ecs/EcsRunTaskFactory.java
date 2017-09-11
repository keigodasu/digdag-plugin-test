package io.digdag.plugin.ecs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClient;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.RunTaskRequest;
import com.amazonaws.services.ecs.model.RunTaskResult;
import io.digdag.client.config.Config;
import io.digdag.spi.*;
import io.digdag.util.BaseOperator;

public class EcsRunTaskFactory implements OperatorFactory {
    @SuppressWarnings("unused")
    private final TemplateEngine templateEngine;

    public EcsRunTaskFactory(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String getType() {
        return "ecs-task";
    }

    @Override
    public Operator newOperator(OperatorContext context) {
        return new EcsRunTaskOperator(context);
    }

    private class EcsRunTaskOperator extends BaseOperator {

        public EcsRunTaskOperator(OperatorContext context) {
            super(context);
        }

        @Override
        public TaskResult runTask() {
            Config params = request.getConfig().mergeDefault(
                    request.getConfig().getNestedOrderedOrGetEmpty("ecs-task")
            );

            String accessKey = params.get("access-key", String.class);
            String secretKey = params.get("secret-key", String.class);
            String ecsClusterArn = params.get("ecs-cluster-arn", String.class);
            String taskDefinitionArn = params.get("task-definition-arn", String.class);

            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

            AmazonECSClientBuilder builder = AmazonECSClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials));
            AmazonECS ecs = builder.build();

            RunTaskRequest request = new RunTaskRequest();
            request.setCluster(ecsClusterArn);
            request.setTaskDefinition(taskDefinitionArn);

            RunTaskResult runResult = ecs.runTask(request);
            System.out.println(runResult.toString());

            return null;
        }
    }
}
