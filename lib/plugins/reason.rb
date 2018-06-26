module Reason
  extend Discordrb::Commands::CommandContainer

  command(:reason, min_args: 2) do |event, caseid, *reason|
    unless %w[Oper Owner Admin Op Half-Op].include? role(event).to_s
      event.channel.send_embed do |e|
        e.title = '**Permission Error**'

        e.description = 'You do not have the proper user modes to do this! You must have +h (half-op) or higher.'
        e.color = 'FF0000'
      end
      next
    end
    event.message.delete
    cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
    message = bot.channel(210_174_983_278_690_304).message(cases[caseid.to_i].to_i).to_s
    joe = message.split("\n")
    if joe[0].include? 'User Mode Updated'
      joe[3] = 'Reason: ' + reason.join(' ')
    else
      joe[2] = 'Reason: ' + reason.join(' ')
      joe[3] = "Responsible staff: #{event.user.mention}" if joe[3].include? '[unknown]'
    end
    edited = joe.join("\n")
    bot.channel(210_174_983_278_690_304).message(cases[caseid.to_i].to_i).edit edited
    event.send_temporary_message("Reason for case #{caseid} set to: #{reason.join(' ')}", 10)
  end
end
