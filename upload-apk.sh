# Reference: https://github.com/the-dagger/open-event-android/blob/updateTools/upload-apk.sh

#create new directory to store the generated apk
mkdir $HOME/buildApk/

#copy generated apk from build folder to the folder just created
cp -R app/build/outputs/apk/debug/*.apk $HOME/buildApk/
cp -R app-legacy/build/outputs/apk/debug/*.apk $HOME/buildApk/

#go to home and git setup  
cd $HOME
git config --global user.email "noreply@travis.com"
git config --global user.name "Travis-CI"

# Clone the repository in the folder buildApk
git clone --quiet --branch ci-builds https://midhunhk:$GITHUB_API_KEY@github.com/midhunhk/message-counter.git ci-builds > /dev/null

#go into directory and copy data we're interested
cd ci-builds 
# delete any existing apks already present
rm *.apk
# copy the latest debug apk 
cp -Rf $HOME/buildApk/* ./

#Create a new branch that will contains only latest apk
git checkout --orphan latest-apk-only

#add, commit and push files
git add -f . 
git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed [skip ci]"

#delete the ci-builds branch
git branch -D ci-builds
#rename the current branch
git branch -m ci-builds

git push origin ci-builds -fq> /dev/null
echo -e "Pushed latest apk \ n "
