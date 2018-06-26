module FJoin
  extend Discordrb::Commands::CommandContainer

  command(:fjoin, min_args: 2, max_args: 2) do |event, mention, join|
    unless %w[Oper].include? role(event).to_s
      event.channel.send_embed do |e|
        e.title = '**Permission Error**'

        e.description = 'You do not have the proper user modes to do this! You must have +Y (oper) or higher.'
        e.color = 'FF0000'
      end
      next
    end
    join.downcase!
    event.message.delete
    allow = Discordrb::Permissions.new
    allow.can_read_messages = true
    deny = Discordrb::Permissions.new
    userid = bot.parse_mention(mention.to_s).id.to_i
    user = event.server.member(userid)

    case join
    when 'gaming'
      id = 424_379_734_483_533_845
    when 'crypto'
      id = 424_379_772_219_424_779
    when 'programming'
      id = 424_379_786_006_364_161
    when 'tech'
      id = 424_379_920_924_278_784
    when 'music'
      id = 424_379_929_539_641_345
    when 'pets'
      id = 424_379_940_604_084_224
    when 'memes'
      id = 424_379_961_256_968_192
    when 'lgbt'
      id = 424_663_031_482_679_316
    when 'anime'
      id = 425_422_889_374_842_891
    else
      event.send_temporary_message('Invalid channel to join!', 5)
      break
    end

    bot.channel(id).define_overwrite(user, allow, deny)
    bot.send_message(id, "*#{user.mention} has joined the channel!*")
  end
end
