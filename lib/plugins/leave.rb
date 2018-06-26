module Leave
  extend Discordrb::Commands::CommandContainer

  command(%i[leave l part p]) do |event|
    event.message.delete
    unless [424_379_734_483_533_845, 424_379_772_219_424_779, 424_379_786_006_364_161, 424_379_920_924_278_784, 424_379_929_539_641_345, 424_379_940_604_084_224, 424_379_961_256_968_192, 424_663_031_482_679_316, 425_422_889_374_842_891].include? event.channel.id
      event.send_temporary_message('You can only leave community channels!', 5)
      break
    end
    event.channel.delete_overwrite(event.user.id)
    event.respond "*#{event.user.mention} has left the channel!*"
  end
end
