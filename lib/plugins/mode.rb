module Mode
  extend Discordrb::Commands::CommandContainer

  command(:mode, min_args: 2, max_args: 2) do |event, mention, mode|
    unless %w[Oper Owner Admin].include? role(event).to_s
      event.channel.send_embed do |e|
        e.title = '**Permission Error**'

        e.description = 'You do not have the proper user modes to do this! You must have +a (admin) or higher.'
        e.color = 'FF0000'
      end
      next
    end

    userid = bot.parse_mention(mention.to_s).id.to_i
    user = event.server.member(userid)
    if mode.length > 2
      add = mode[0..0] == '+'
      modetemp = mode[1..mode.length]
      modes = modetemp.split('')
      if add
        modes.each do |meme|
          meme = "+#{meme}"
          to_add = event.server.roles.find { |role| role.name == meme }
          user.add_role(to_add)
        end
      else
        modes.each do |meme|
          meme = "+#{meme}"
          to_remove = event.server.roles.find { |role| role.name == meme }
          user.remove_role(to_remove)
        end
      end
    elsif mode[0..0] == '+'
      to_add = event.server.roles.find { |role| role.name == mode }
      user.add_role(to_add)
    elsif mode[0..0] == '-'
      actualmode = mode.tr('-', '+')
      to_remove = event.server.roles.find { |role| role.name == actualmode }
      user.remove_role(to_remove)
    elsif mode == '+*'
      to_add = event.user.role?(event.server.roles.find { |role| role.name == '+B' })
      user.add_role(to_add)
      to_add = event.user.role?(event.server.roles.find { |role| role.name == '+Q' })
      user.add_role(to_add)
      to_add = event.user.role?(event.server.roles.find { |role| role.name == '+d' })
      user.add_role(to_add)
      to_add = event.user.role?(event.server.roles.find { |role| role.name == '+m' })
      user.add_role(to_add)
      to_add = event.user.role?(event.server.roles.find { |role| role.name == '+e' })
      user.add_role(to_add)
      mode = '+BQdme'
    else
      next
    end
    event.channel.send_embed do |e|
      e.title = '**User Mode Changed Successfully**'

      e.description = "Usermode has been set to #{mode}."
      e.color = '00FF00'
    end
    cases = File.readlines('cases.txt') { |line| line.split.map(&:to_s).join }
    message = bot.channel(210_174_983_278_690_304).send_message [
      "**User Mode Updated** | Case ##{cases.length}",
      "User: #{user.name}##{user.discrim} (#{user.mention})",
      "Mode: #{mode}",
      'Reason: Responsible staff please add reason by `;reason case# [reason]`',
      "Responsible staff: #{event.user.mention}"
    ].join("\n")
    filename = 'cases.txt'
    File.open(filename, 'a+') { |f| f.puts(message.id.to_s) }
  end
end
