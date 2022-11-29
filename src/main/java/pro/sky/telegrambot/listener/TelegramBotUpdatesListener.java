package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Task;
import pro.sky.telegrambot.repository.TaskRepository;

import javax.annotation.PostConstruct;
import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private TaskRepository taskRepository;

    public TelegramBotUpdatesListener(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) throws RuntimeException {
        String dateTextRegex = "([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)";
        Pattern pattern = Pattern.compile(dateTextRegex);

        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message().text().equals("/start")) {
                SendMessage message = new SendMessage(update.message().chat().id(),
                        "Привет " + update.message().chat().firstName());
                SendResponse response = telegramBot.execute(message);

//                taskRepository.save(new Task(48L, "lolo", LocalDateTime.now()));

            } else {
                Matcher matcher = pattern.matcher(update.message().text());
                if (matcher.matches()) {
                    try {
                        String date = matcher.group(1);
                        String text = matcher.group(3);
                        Task task = new Task(update.message().chat().id(), text, LocalDateTime.parse(date,
                                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                        taskRepository.save(task);
                    } catch (RuntimeException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void dataScanner() {
        List<Task> tasks = taskRepository.findAllByDataTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        if (tasks.size() != 0) {

            tasks.forEach(task -> {
                SendMessage message = new SendMessage(task.getChatId(), "Дорогой, пора: " + task.getText());
                SendResponse response = telegramBot.execute(message);

//                taskRepository.delete(task);
            });
        }
    }

}
