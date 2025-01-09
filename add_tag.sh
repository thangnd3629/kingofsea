export KOS_TAG=v$(cat ./VERSION.txt)
git push origin --delete $KOS_TAG
git tag -d $KOS_TAG
git tag -a v$(cat ./VERSION.txt) -m "$(cat CHANGELOG.txt)"
git push origin $KOS_TAG