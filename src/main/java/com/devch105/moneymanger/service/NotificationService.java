package com.devch105.moneymanger.service;

import com.devch105.moneymanger.dto.ExpenseDTO;
import com.devch105.moneymanger.entity.ProfileEntity;
import com.devch105.moneymanger.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final EmailService emailService;
    private final ProfileRepository  profileRepository;

    @Value("${money.manager.frontend.url}")
    private String frontendURl;

    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Kolkata")

    public  void sendIncomeAndExpenseRemiderNotification(){
        log.info("Income and expense Job is Started");
        List<ProfileEntity> profiles = profileRepository.findAll();

        for(ProfileEntity profile : profiles){
            String body =
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; "
                            + "padding: 20px; border: 1px solid #e5e7eb; border-radius: 10px;'>"

                            + "<h2 style='color: #2563eb;'>💰 Money Manager</h2>"

                            + "<p>Hi <strong>" + profile.getFullName() + "</strong>,</p>"

                            + "<p>"
                            + "This is a friendly reminder to add your income and expenses for today."
                            + "</p>"

                            + "<p>"
                            + "Keeping your records updated daily helps you track your spending habits "
                            + "and maintain better control over your finances."
                            + "</p>"

                            + "<div style='text-align: center; margin: 30px 0;'>"
                            + "<a href='" + frontendURl + "' "
                            + "style='background-color: #2563eb; color: white; "
                            + "padding: 12px 24px; text-decoration: none; "
                            + "border-radius: 6px; font-weight: bold;'>"
                            + "Update Today's Records"
                            + "</a>"
                            + "</div>"

                            + "<p style='color: #6b7280;'>"
                            + "Thank you for using Money Manager."
                            + "</p>"

                            + "<p>"
                            + "Best Regards,<br>"
                            + "<strong>Money Manager Team</strong>"
                            + "</p>"

                            + "</div>";
            String subject = "💰 Don't Forget to Track Today's Finances";
            emailService.sendEmail(profile.getEmail(),subject,body);
        }
    }


//    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")
   @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Kolkata")
    public void sendDailyExpenseNotification(){
        log.info("Daily Expense Job is Started");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile : profiles){
            List<ExpenseDTO> expenses  =  expenseService.getExpensesForCurrentUserOnDate(profile.getId(), LocalDate.now(ZoneId.of("Asia/Kolkata")));
            if (!expenses.isEmpty()) {

                StringBuilder table = new StringBuilder();

                table.append("<table style='width:100%; border-collapse:collapse;'>");
                table.append("<thead>");
                table.append("<tr style='background-color:#2563eb; color:white;'>");
                table.append("<th style='padding:10px; border:1px solid #ddd;'>Expense</th>");
                table.append("<th style='padding:10px; border:1px solid #ddd;'>Amount</th>");
                table.append("<th style='padding:10px; border:1px solid #ddd;'>Category</th>");
                table.append("<th style='padding:10px; border:1px solid #ddd;'>Date</th>");
                table.append("</tr>");
                table.append("</thead>");
                table.append("<tbody>");

                double totalExpense = 0;

                for (ExpenseDTO expense : expenses) {

                    totalExpense += expense.getAmount().doubleValue();

                    table.append("<tr>");
                    table.append("<td style='padding:10px; border:1px solid #ddd;'>")
                            .append(expense.getName())
                            .append("</td>");

                    table.append("<td style='padding:10px; border:1px solid #ddd;'>₹")
                            .append(expense.getAmount())
                            .append("</td>");

                    table.append("<td style='padding:10px; border:1px solid #ddd;'>")
                            .append(expense.getCategoryName())
                            .append("</td>");

                    table.append("<td style='padding:10px; border:1px solid #ddd;'>")
                            .append(expense.getDate())
                            .append("</td>");

                    table.append("</tr>");
                }

                table.append("</tbody>");
                table.append("</table>");

                String body =
                        "<div style='font-family:Arial,sans-serif;max-width:700px;margin:auto;padding:20px;'>"

                                + "<h2 style='color:#2563eb;'>📊 Daily Expense Summary</h2>"

                                + "<p>Hi <strong>" + profile.getFullName() + "</strong>,</p>"

                                + "<p>Here is your expense summary for today.</p>"

                                + "<p><strong>Total Expenses: ₹" + totalExpense + "</strong></p>"

                                + table

                                + "<div style='text-align:center;margin-top:30px;'>"
                                + "<a href='" + frontendURl + "' "
                                + "style='background:#2563eb;color:white;padding:12px 24px;"
                                + "text-decoration:none;border-radius:6px;'>"
                                + "View Dashboard"
                                + "</a>"
                                + "</div>"

                                + "<p style='margin-top:20px;'>Keep tracking your expenses and stay on top of your finances! 💰</p>"

                                + "<p>Regards,<br><strong>Money Manager Team</strong></p>"

                                + "</div>";

                String subject = "📊 Your Daily Expense Summary";

                emailService.sendEmail(profile.getEmail(), subject, body);
                log.info("Mail has been sent to " + profile.getEmail());
            }
        }
    }
}
