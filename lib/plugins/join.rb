module Join
  extend Discordrb::Commands::CommandContainer

  command(%i[join j], min_args: 1, max_args: 1) do |event, join|
    join.downcase!
    event.message.delete
    allow = Discordrb::Permissions.new
    allow.can_read_messages = true
    deny = Discordrb::Permissions.new

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

    Bot.channel(id).define_overwrite(event.user, allow, deny)
    Bot.send_message(id, "*#{event.user.mention} has joined the channel!*")
  end
end
