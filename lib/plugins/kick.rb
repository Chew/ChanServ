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

    userid = Bot.parse_mention(mention.to_s).id.to_i
    user = event.server.member(userid)
    event.server.kick(userid)
    event.channel.send_embed do |e|
      e.title = '**User Kicked Successfully**'

      e.description = "Say goodbye to that loser #{user.name}."
      e.color = '00FF00'
    end
    cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
    message = Bot.channel(210_174_983_278_690_304).send_embed do |embed|
      embed.title = "Kick | Case ##{cases.length}"
      embed.colour = 0xd084

      embed.add_field(name: 'User', value: "#{user.distinct} (#{user.mention})", inline: true)
      embed.add_field(name: 'resp staff', value: event.user.mention, inline: true)
      embed.add_field(name: 'Reason', value: 'Responsible staff please add reason by `;reason case# [reason]`', inline: true)
    end
    filename = 'cases.txt'
    File.open(filename, 'a+') { |f| f.puts(message.id.to_s) }
  end
end
