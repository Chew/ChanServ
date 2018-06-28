module Admin
  extend Discordrb::Commands::CommandContainer

  command(:admin, min_args: 1, max_args: 1) do |event, mention|
    unless %w[Oper Owner].include? role(event).to_s
      event.channel.send_embed do |e|
        e.title = '**Permission Error**'

        e.description = 'You do not have the proper user modes to do this! You must have +q (Owner) or higher.'
        e.color = 'FF0000'
      end
      next
    end

    userid = Bot.parse_mention(mention.to_s).id.to_i
    user = event.server.member(userid)
    to_add = event.server.roles.find { |role| role.name == 'Admins' }
    user.add_role(to_add)

    event.channel.send_embed do |e|
      e.title = '**User Mode Changed Successfully**'

      e.description = "#{user.mention} has been promoted to admin by #{event.user.mention}."
      e.color = '00FF00'
    end
    cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
    message = Bot.channel(210_174_983_278_690_304).send_message [
      "**User Mode Updated** | Case ##{cases.length}",
      "User: #{user.name}##{user.discrim} (#{user.mention})",
      'Mode: Admin (+a)',
      'Reason: Responsible staff please add reason by `;reason case# [reason]`',
      "Responsible staff: #{event.user.mention}"
    ].join("\n")
    filename = 'cases.txt'
    File.open(filename, 'a+') { |f| f.puts(message.id.to_s) }
  end
end
