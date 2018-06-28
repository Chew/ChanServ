module FLeave
  extend Discordrb::Commands::CommandContainer

  command(:fleave) do |event, join|
    event.message.delete
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
      event.send_temporary_message('Invalid channel!', 5)
      break
    end
    unless [424_379_734_483_533_845, 424_379_772_219_424_779, 424_379_786_006_364_161, 424_379_920_924_278_784, 424_379_929_539_641_345, 424_379_940_604_084_224, 424_379_961_256_968_192, 424_663_031_482_679_316, 425_422_889_374_842_891].include? event.channel.id
      event.send_temporary_message('You can only leave community channels!', 5)
      break
    end
    Bot.channel(id).delete_overwrite(event.user.id)
    event.respond "*#{event.user.mention} has left the channel!*"
  end
end
