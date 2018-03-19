package ru.alcereo.vote.frontend;


import ru.alcereo.vote.VoteCommunicationService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame implements VoteFrontend {

    private JFrame frame;
    private String userName;
    private JPanel centerPanel;
    private List<OptionFrame> options = new ArrayList<>();

    private JPanel controlPanel;
    private JTextField optionField;
    private JButton addBtn;
    private JButton voteBtn;
    private VoteCommunicationService votingService;
    private volatile boolean voted = false;

    public MainFrame(String userName, VoteCommunicationService votingService) {
        this.userName = userName;
        this.votingService = votingService;

        votingService.registerFrontend(userName, this);

        setupUI();

        frame.setVisible(true);
    }

    private void setupUI() {
        frame = new JFrame(userName);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        setupControlPanel(controlPanel);

        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.add(
                new JLabel("Vote options", SwingConstants.CENTER),
                BorderLayout.NORTH
        );
        frame.add(centerPanel, BorderLayout.LINE_START);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setSize(300, 500);
        frame.setLocationRelativeTo(null);

    }

    private void setupControlPanel(JPanel controlPanel) {

        optionField = new JTextField(10);
        addBtn = new JButton("Add");
        voteBtn = new JButton("Vote");

        controlPanel.add(optionField);
//        controlPanel.add(addBtn);
        controlPanel.add(voteBtn);

//        addBtn.addActionListener(e -> sendAddOption());
        voteBtn.addActionListener(e -> sendVote());
    }

    private void sendVote() {
        String option = optionField.getText();
        optionField.setText("");

        votingService.sendVote(userName, option);
    }

//    private void sendAddOption() {
//        String option = optionField.getText();
//        optionField.setText("");
//
//        votingService.sendAddOption(option);
//    }

    public void addOption(String name){
        createOption(name);
    }

    private OptionFrame createOption(String name){
        OptionFrame option = new OptionFrame(name);
        centerPanel.add(option);
        options.add(option);
        return option;
    }

    @Override
    public void setOptionVotes(String name, Integer count){
        options.stream()
                .filter(
                        optionFrame -> optionFrame.getOptionName().equals(name)
                ).findFirst()
                .orElseGet(() -> createOption(name))
                .setVotes(count);
    }

    static class OptionFrame extends JPanel{

        private String optionName;

        JLabel nameLabel = new JLabel("unnamed");
        JLabel votesCountLabel = new JLabel("0");

        public OptionFrame(String name) {
            this.optionName = name;
            nameLabel.setText(name);
            add(nameLabel);
            add(new JLabel(" - "));
            add(votesCountLabel);
        }

        public void setVotes(Integer count){
            votesCountLabel.setText(count.toString());
        }

        public String getOptionName() {
            return optionName;
        }
    }
}
