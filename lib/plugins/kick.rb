module Kick
  extend Discordrb::Commands::CommandContainer

  command(:kick, min_args: 1, max_args: 1) do |event, mention|
    unless %w[Oper Owner Admin Ops Half-Op].include? role(event).to_s
      event.channel.send_embed do |e|
        e.title = '**Permission Error**'

        e.description = 'You do not have the proper user modes to do this! You must have +h (half-op) or higher.'
        e.color = 'FF0000'
      end
      next
    end

    userid = bot.parse_mention(mention.to_s).id.to_i
    user = event.server.member(userid)
    event.server.kick(userid)
    event.channel.send_embed do |e|
      e.title = '**User Kicked Successfully**'

      e.description = "Say goodbye to that loser #{user.name}."
      e.color = '00FF00'
    end
    cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
    message = bot.channel(210_174_983_278_690_304).send_message [
      "**Kick** | Case ##{cases.length}",
      "User: #{user.name}##{user.discrim} (#{user.mention})",
      'Reason: Responsible staff please add reason by `;reason case# [reason]`',
      "Responsible staff: #{event.user.mention}"
    ].join("\n")
    filename = 'cases.txt'
    File.open(filename, 'a+') { |f| f.puts(message.id.to_s) }
  end
end
